package be.afhistos.satellitev2;

public class ShutdownBot implements Runnable {

    @Override
    public void run() {
        System.out.println("ShutdownNow JDA");
        Satellite.getBot().shutdownNow();
        System.out.println("Shutdown Threadpool");
        StartHandler.getThreadPool().shutdown();
        System.exit(0);
    }
}
