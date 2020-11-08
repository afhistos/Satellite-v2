package be.afhistos.satellitev2;

public class ShutdownBot implements Runnable {

    @Override
    public void run() {
        StartHandler.getConsoleThread().interrupt();
        Satellite.getBot().shutdownNow();
        StartHandler.getMainThread().interrupt();
        System.exit(0);
    }
}
