package be.afhistos.satellitev2;

import java.io.IOException;

public class ShutdownBot implements Runnable {

    @Override
    public void run() {
        if(StartHandler.getServer() != null){
            StartHandler.getVulcainThread().interrupt();
        }
        StartHandler.getConsoleThread().interrupt();
        Satellite.getBot().shutdownNow();
        StartHandler.getMainThread().interrupt();
        System.exit(0);
    }
}
