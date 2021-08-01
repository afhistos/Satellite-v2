package be.afhistos.satellitev2;

public class ShutdownBot implements Runnable {

    @Override
    public void run() {
        if(StartHandler.getServer() != null){
            StartHandler.getVulcainThread().interrupt();
        }
        StartHandler.getConsoleThread().interrupt();
        Satellite.getBot().shutdownNow();
        StartHandler.getMainThread().interrupt();
    }
}
