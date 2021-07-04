package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.StartHandler;

import java.io.IOException;

public class ServerThread extends Thread {

    @Override
    public void run() {
        StartHandler.getServer().run();
    }

    @Override
    public void interrupt() {
        try {
            StartHandler.getServer().stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
