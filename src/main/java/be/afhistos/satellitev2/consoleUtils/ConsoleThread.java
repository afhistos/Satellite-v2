package be.afhistos.satellitev2.consoleUtils;

import java.util.Scanner;

public class ConsoleThread extends Thread{
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (scanner.hasNext()){
            if(scanner.next().equalsIgnoreCase("stop")){
                System.out.println("Arrêt en cours...");
                scanner.close();
                be.afhistos.satellitev2.Satellite.setRunning(false);
                this.interrupt();
                break;
            } else if (scanner.next().equalsIgnoreCase("restartServer")){
            }else if (scanner.next().equalsIgnoreCase("startServer")){
            }else if (scanner.next().equalsIgnoreCase("stopServer")){
            }
        }
    }


}
