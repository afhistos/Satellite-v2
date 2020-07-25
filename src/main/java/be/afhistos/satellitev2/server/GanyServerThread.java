package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class GanyServerThread implements Runnable {

    private Map<Integer, WorkerRunnable> clients = new HashMap<>();

    private int serverPort = 5656;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;

    public GanyServerThread(int port){
        this.serverPort = port;
    }

    public void run() {
         synchronized (this){
             this.runningThread = Thread.currentThread();
         }
         openServerSocket();
         while(!isStopped){
             Socket clientSocket = null;
             try{
                 clientSocket = this.serverSocket.accept();
             } catch (IOException e) {
                 if(isStopped){
                     BotUtils.log(LogLevel.INFO, "Serveur de données éteint", true, true);
                     return;
                 }
                 BotUtils.log(LogLevel.ERROR, "Une erreur est survenue lors de la connexion d'un client", true,true);
             }
             WorkerRunnable worker = new WorkerRunnable(clientSocket);
             clients.put(clientSocket.getPort(), worker);
             new Thread(worker,"Multithreaded server connection").start();
         }
         BotUtils.log(LogLevel.INFO, "Serveur de données éteint", true, true);
    }

    /**
     *
     * @return true if server isn't running anymore
     */
    public boolean isStopped() {
        return isStopped;
    }

    synchronized public void stop() {
        this.isStopped=  true;
        getClientsStream().forEach(client -> {
            try {
                client.getClientSocket().close();
            } catch (IOException e) {
                if(!client.getClientSocket().isClosed()){
                    BotUtils.log(LogLevel.ERROR, "Impossible de fermer la connexion avec le client "+
                            client.getClientSocket().getInetAddress().getCanonicalHostName(), true, true);
                }
            }
        });
        try{
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la fermeture du serveur de données", e);
        }
    }

    /**
     *
     * @param s the String to send to all clients
     */
    public void sendToClients(String s){
        getClientsStream().forEach(client -> client.sendToClient(s));
    }

    public void sendToClient(int port, String s){
        getClientsStream().filter(workerRunnable -> workerRunnable.getClientSocket().getPort() == port)
                .forEach(workerRunnable -> workerRunnable.sendToClient(s));

    }

    public void removeClient(int clientPort){
        BotUtils.log(LogLevel.INFO, "Removed client "+clientPort, true, true);
        WorkerRunnable worker = clients.remove(clientPort);
        if(worker == null){
            return; //Can't find the client Thread, already disconnected / never logged in
        }
        //check if the socked is closed
        if(worker.getClientSocket().isConnected() || !worker.getClientSocket().isClosed()){
            try {
                worker.getClientSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void openServerSocket(){
        try{
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            BotUtils.log(LogLevel.ERROR, "Impossible d'ouvrir le serveur sur le port "+this.serverPort, true,true);
        }
    }

    /**
     *
     * @return a stream of all connected clients
     */
    synchronized public Stream<WorkerRunnable> getClientsStream(){
         return clients.values().stream();
    }





}
