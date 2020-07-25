package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.StartHandler;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.commons.utils.TableBuilder;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    private BufferedReader in;
    private PrintWriter out;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientSocket.setKeepAlive(true);
        } catch (IOException e) {
            BotUtils.log(LogLevel.ERROR, "Impossible d'établir une communication avec le client "+clientSocket.getInetAddress().getHostAddress()+
                    ":"+clientSocket.getPort(), true, true);
        }
        String inputline;
        while((clientSocket.isConnected() || !clientSocket.isClosed()) && !(StartHandler.getServerInstance().isStopped())){
            try {
                if(!clientSocket.isClosed() &&((inputline = in.readLine()) != null)){
                    treat(inputline);
                }
            } catch (SocketException ign){
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        //Client disconnected
        StartHandler.getServerInstance().removeClient(clientSocket.getPort());
    }

    private void treat(String s) {
        System.out.println("TREAT "+s);
        if(s.startsWith("list")) {//afficher la liste des addresses connectées
            List<List<String>> list = new ArrayList<>();
            StartHandler.getServerInstance().getClientsStream().forEach(worker -> {
                list.add(Arrays.asList(worker.getClientSocket().getInetAddress().getHostAddress(),
                        String.valueOf(worker.getClientSocket().getPort())));
            });
            String[][] values = new String[list.size()][];
            int i = 0;
            for (List<String> nestedList : list) {
                values[i++] = nestedList.toArray(new String[nestedList.size()]);
            }

            TableBuilder tb = new TableBuilder().setAlignment(TableBuilder.Alignment.CENTER)
                    .setBorders(TableBuilder.Borders.newPlainBorders("-", "|", "+"))
                    .setName("Liste des connexions").addHeaders("Adresse", "Port").setValues(values);
            System.out.println(tb.build());

        }else if(s.startsWith("time")){//envoyer la date complète
            String time = BotUtils.getFullTimestamp(System.currentTimeMillis());
            out.println(time);
        }

    }

    public void sendToClient(String s){
        System.out.println("Sending "+s);
        out.println(s);
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

}
