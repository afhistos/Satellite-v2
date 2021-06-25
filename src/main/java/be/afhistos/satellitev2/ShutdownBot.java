package be.afhistos.satellitev2;

import java.io.IOException;

public class ShutdownBot implements Runnable {

    @Override
    public void run() {
        if(StartHandler.getServer() != null){
            try {
                StartHandler.getServer().stop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        StartHandler.getConsoleThread().interrupt();
        Satellite.getBot().shutdownNow();
        StartHandler.getMainThread().interrupt();
        System.exit(0);
    }
}
