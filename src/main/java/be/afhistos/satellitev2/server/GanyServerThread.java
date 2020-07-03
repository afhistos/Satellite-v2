package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GanyServerThread extends Thread {

    private boolean linked = false, poweringOff = false;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private ServerSocket socket= null;
    private Socket clientSocket = null;

    public GanyServerThread(){}

    @Override
    public void run() {
         socket = null;
        try{
            socket = new ServerSocket(2310);
        } catch (IOException e) {
            BotUtils.log(LogLevel.SYSTEM,"Impossible d'écouter le port 2310 ! Arrêt du système...", true, true);
            return;
        }
        BotUtils.log(LogLevel.INFO,"En attente de connexion...", true,false);
        try{
            clientSocket = socket.accept();
        } catch (IOException e) {
            BotUtils.log(LogLevel.SYSTEM, "Échec de l'autorisation de connexion. Arrêt du système...", true,true);
            return;
        }
        BotUtils.log(LogLevel.INFO, "Connexion reçue de "+clientSocket.getInetAddress().getHostAddress()+
                ":"+clientSocket.getPort(), true, true);

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            BotUtils.log(LogLevel.ERROR, "Une erreur est survenue lors de l'initialisation des canaux de communication."
                +"Arrêt du système...", true,true);
            return;
        }
        String inputLine, outputLine;
        outputLine = "ConnTest";
        System.out.println(outputLine);
        out.println(outputLine);
        while (!poweringOff) {
            try {
                if (((inputLine = in.readLine()) != null)) {
                    treat(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void treat(String s) {
        if(s.equals("ConnTestTrue")){
            linked = true;
            BotUtils.log(LogLevel.INFO, "Test de connexion accepté.", true, false);
            return;
        }
    }

    public void shutdown(){
        poweringOff = true;
        try {
            in.close();
            out.close();
            if (clientSocket.isConnected()) {
                clientSocket.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BotUtils.log(LogLevel.INFO, "Serveur éteint", true, false);
    }

    public boolean isLinked() {
        return linked;
    }

    public void send(String s){
        out.println(s);
    }



}
