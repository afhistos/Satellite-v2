package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SatelliteServer extends Thread{
    private int port;
    private ServerSocket serverSocket;
    public int SLEEP_TIME = 750;
    private ArrayList<SatelliteServerThread> clients;
    private File logs;
    private Writer dataWriter;

    public SatelliteServer(int port){
        this.setName("Vulcain-Server");
        this.port = port;
        clients = new ArrayList<>();
        logs = new File("vulcain-server.log");
        if(!logs.exists()){
            try {
                logs.createNewFile();
                dataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logs), "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void run() {
        super.run();
        try{
            serverSocket = new ServerSocket(port);
            BotUtils.log(LogLevel.INFO, "Serveur Vulcain à l'écoute du port "+serverSocket.getLocalPort(), true, false);
            while(true){
                SatelliteServerThread sst = new SatelliteServerThread(serverSocket.accept(), SLEEP_TIME);
                clients.add(sst);
                sst.run();
            }
        } catch(SocketException ign) {//Socket closed
        } catch (IOException e) {
            BotUtils.log(LogLevel.ERROR, "Impossible de travailler sur le port "+port+"!\nAvez-vous essayé un autre port ?", true, true);

        }

    }

    @Override
    public void interrupt() {
        super.interrupt();
        for(SatelliteServerThread client : clients){
            client.exit();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean disconnect(SatelliteServerThread client){
        client.exit();
        return clients.remove(client);
    }

    public void log(String s){
        StringBuilder sb = new StringBuilder("[");
        sb.append(BotUtils.getTimestamp(System.currentTimeMillis(), false)).append("] ");
        sb.append(s);

    }

}
