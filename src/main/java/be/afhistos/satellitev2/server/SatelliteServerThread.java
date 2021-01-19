package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.StartHandler;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SatelliteServerThread{
    private Socket socket = null;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Queue<VulcainPacket> packetList;
    private ServerPacketHandler serverCommandHandler;
    private int sleep;
    private ScheduledExecutorService executor;
    private Future executorFuture;
    private String guildId;

    public SatelliteServerThread(Socket s, int sleep){
        BotUtils.log(LogLevel.INFO, "Nouvelle connexion de "+((InetSocketAddress)s.getRemoteSocketAddress()).getAddress(), true, true);
        socket = s;
        this.sleep = sleep;
        packetList = new LinkedList<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        serverCommandHandler = new ServerPacketHandler(this);
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            //Send GuildList
            List<GuildInfo> guildInfoList = new ArrayList<>();
            for (Guild g : Satellite.getBot().getGuilds()) {
                guildInfoList.add(new GuildInfo(g.getName(), g.getId(), g.getOwner().getEffectiveName()));
            }
            VulcainPacket packetOut = new VulcainPacket("GuildList", guildInfoList);
            out.writeObject(packetOut);
            VulcainPacket packet = (VulcainPacket) in.readObject();
            if(packet.getKey().equalsIgnoreCase("define guildid")){
                guildId = (String) packet.getValue();
            }
            BotUtils.log(LogLevel.CONFIG, "guildId: "+guildId, true, false);

        } catch (IOException | ClassNotFoundException e) {

                e.printStackTrace();
        }
    }

    public void run(){
        executorFuture = executor.scheduleAtFixedRate(() ->{
            VulcainPacket now;
            System.out.println("run");
            for (int i = 0; i < packetList.size(); i++) {
                System.out.println("Packet to send: "+i);
                try {
                    now = packetList.poll();
                    StartHandler.getSatelliteServer().log("PACKET_OUT_SENT : K:"+now.getKey()+"   V:"+now.getValue().toString());
                    out.writeObject(now);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println("tryto handle");
                while(in.available() > 0){
                    System.out.println("handle");
                    serverCommandHandler.handlePacket((VulcainPacket)in.readObject());
                }
                System.out.println("check conn");
                if(socket.isClosed() || !socket.isConnected()){
                    System.out.println("Socket closed");
                    exit();
                }
                //Detect crashes (https://stackoverflow.com/a/17888062)
                /*try{
                    in.read();
                }catch (Exception ign){
                    System.out.println("-----CRASH-----");
                    exit();
                }

                 */
            }catch (SocketException ign){//Force disconnected
                BotUtils.log(LogLevel.WARNING, "connection abortion", true, false);
                exit();
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        },0,sleep, TimeUnit.MILLISECONDS);

    }

    public synchronized void sendPacket(VulcainPacket v){
        packetList.offer(v);
        StartHandler.getSatelliteServer().log("PACKET_OUT_REGISTERED : K:"+v.getKey()+"   V:"+v.getValue().toString());
    }

    public boolean exit(){
        sendPacket(new VulcainPacket("Disconnect", "Disconnect"));
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorFuture.cancel(true);
        executor.shutdown();
        return executorFuture.isCancelled();
    }

}
