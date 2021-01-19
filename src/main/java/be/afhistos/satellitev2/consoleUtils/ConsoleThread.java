package be.afhistos.satellitev2.consoleUtils;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.StartHandler;
import be.afhistos.satellitev2.server.SatelliteServer;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleThread extends Thread{
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (scanner.hasNext()){
            if(scanner.next().equalsIgnoreCase("stop")){
                System.out.println("Arrêt en cours...");
                scanner.close();
                Satellite.setRunning(false);
                this.interrupt();
                break;
            } else if (scanner.next().equalsIgnoreCase("restartServer")){

                BotUtils.restartVulcain();
            }else if (scanner.next().equalsIgnoreCase("startServer")){
                BotUtils.startVulcain();
            }else if (scanner.next().equalsIgnoreCase("stopServer")){
                BotUtils.stopVulcain();
            }
        }
    }


}
