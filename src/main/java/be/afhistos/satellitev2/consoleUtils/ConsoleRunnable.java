package be.afhistos.satellitev2.consoleUtils;

import be.afhistos.satellitev2.Satellite;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleRunnable implements Runnable{
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<ConsoleListener> listeners = new ArrayList<>();

    @Override
    public void run() {
        while (scanner.hasNext()){
            String s = scanner.next();
            listeners.forEach(listener -> listener.onConsoleCommand(s));
            if(s.equalsIgnoreCase("stop")){
                System.out.println("Arrêt en cours...");
                scanner.close();
                Satellite.setRunning(false);
                System.out.println("End of console");
                break;
            } else if (s.equalsIgnoreCase("restartServer")){
            }else if (s.equalsIgnoreCase("startServer")){
            }else if (s.equalsIgnoreCase("stopServer")){
            }
        }
    }

    public void addListener(ConsoleListener listener){
        listeners.add(listener);
    }

    public boolean removeListener(ConsoleListener listener){
         return listeners.remove(listener);
    }


}
