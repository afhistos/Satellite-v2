package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerThread extends Thread{
    private int port;
    private ServerSocket serverSocket;
    private ArrayList<ServerCommunicationThread> clients;
    private File logs;
    private Writer dataWriter;

    public int SLEEP_TIME = 250;

    public ServerThread(int port){
        this.setName("Vulcain-Server");
        System.out.println("ServerThread init");
        this.port = port;
        clients = new ArrayList<ServerCommunicationThread>();
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
    public synchronized void start() {
        super.start();
        log("Démarrage du serveur. Éspérons que le voyage se passe bien.");
        System.out.println("ServerThread start");
        try{
            serverSocket= new ServerSocket(port);
            log("Serveur démarré sur le port "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                ServerCommunicationThread sct = new ServerCommunicationThread(serverSocket.accept());
                log("Nouvelle connexion depuis "+BotUtils.getIpAddress(sct.getSocket()));
                clients.add(sct);
                sct.start();
            } catch (SocketException ign){ //Socket closed
                log("Le serveur s'est arrêté de manière innatendue. Arrêt complet du service...");
                this.interrupt();
            }catch (IOException ign) { //Port already in use
                log("Impossible de travailler sur le port "+port+". Arrêt complet du service...");
                this.interrupt();
            }
            /*
            finally {
                try {
                    dataWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            A TESTER
             */
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if(serverSocket == null || serverSocket.isClosed()){//thread interrupted by error
            clients.clear();
            BotUtils.log(LogLevel.ERROR, "Le serveur Vulcain s'est arrêté subitement.", true, true);
        }else{
            for(ServerCommunicationThread client: clients){
                String ip = BotUtils.getIpAddress(client.getSocket());
                boolean success = logout(client);
                log("Déconnexion propre du client "+ip+(success ? " réussie.":"échouée."));
            }
            clients.clear();
            try{
                serverSocket.close();
            } catch (IOException ign) {}//Since the server is stopping, we don't matter about errors
        }
        log("Arrêt.");
        try {
            dataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param client The client to logout
     * @return true if the client was registered and is not anymore
     */
    public boolean logout(ServerCommunicationThread client){
        client.interrupt();
        return clients.remove(client);
    }


    public void log(String s){
        StringBuilder sb = new StringBuilder("[");
        sb.append(BotUtils.getFullDate(System.currentTimeMillis())).append("] ");
        sb.append(s);
        try {
            dataWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
