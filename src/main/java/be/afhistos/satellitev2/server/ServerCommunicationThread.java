package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.StartHandler;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ServerCommunicationThread extends Thread{
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread inThread;
    private Queue<VulcainPacket> packetQueue;
    private ServerPacketHandler serverPacketHandler;
    private String guildId;

    public ServerCommunicationThread(Socket s) {
        this.s = s;
        packetQueue = new LinkedList<>();
    }

    @Override
    public synchronized void start() {
        super.start();
        List<GuildInfo> guildInfoList = new ArrayList<>();
        for (Guild g : Satellite.getBot().getGuilds()) {
            guildInfoList.add(new GuildInfo(g.getName(), g.getId(), g.getOwner().getEffectiveName()));
        }
        serverPacketHandler = new ServerPacketHandler(this);
        try{
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
            //Send list of the guilds( without it, no actions sent by client will take place)
            inThread = new ServerCommunicationInThread(this);
            inThread.start();
            out.writeObject(new VulcainPacket(PacketType.PACKET_SERVER,"GuildList", guildInfoList));
        } catch (IOException e) {
            StartHandler.getServerThread().log("Une erreur est survenue lors de l'établissement de la communication avec "+
                    BotUtils.getIpAddress(s)+":\n"+e.getStackTrace().toString());

        }
    }

    @Override
    public void run() {
        super.run();

        while(true){
            packetQueue.iterator().forEachRemaining(vulcainPacket -> {
                try {
                    out.writeObject(vulcainPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                Thread.sleep(StartHandler.getServerThread().SLEEP_TIME);
            } catch (InterruptedException ign) {}
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        sendPacket(new VulcainPacket(PacketType.PACKET_SERVER,"Disconnect","Disconnect"));
        try{
            inThread.interrupt();
            in.close();
            out.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        packetQueue.clear();

    }

    public void sendPacket(VulcainPacket packet){
        packetQueue.offer(packet);
    }

    public Socket getSocket() {
        return s;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public class ServerCommunicationInThread extends Thread{
        private ServerCommunicationThread serverThread;
        private ObjectInputStream in;

        public ServerCommunicationInThread(ServerCommunicationThread serverThread){
            this.serverThread = serverThread;
            in = serverThread.in;
        }

        @Override
        public void run() {
            //Since the readObject() method is blocking, no need to thread-sleep to limit the thread activity
            while(true){
                try {
                    serverThread.serverPacketHandler.handlePacket((VulcainPacket) in.readObject());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
