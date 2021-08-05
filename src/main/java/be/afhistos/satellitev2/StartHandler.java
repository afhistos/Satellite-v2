package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.ConsoleRunnable;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.server.VulcainServer;
import org.java_websocket.server.WebSocketServer;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartHandler {
    private static long startTime;
    private static Properties props = new Properties();
    private static File logFile;
    private static Writer logWriter = null;
    private static Runnable consoleRunnable;
    private static ExecutorService pool;

    private static Satellite satellite;
    private static WebSocketServer vulcain;


    public static void main(String[] args) throws IOException, LoginException, InterruptedException, SQLException {

        startTime = System.currentTimeMillis();
        pool = Executors.newCachedThreadPool();
        props.load(StartHandler.class.getResourceAsStream("/props.properties"));
        BotUtils.log(LogLevel.INFO, "Démarrage de Satellite v"+props.getProperty("version"), true, false);

        logFile = new File("Satellite-log-"+BotUtils.getFullTimestamp(startTime)+".log");
        if(logFile.createNewFile()){
            BotUtils.log(LogLevel.CONFIG, "Fichier de `CommandWatcher` ("+logFile.getName()+") créé!", true, false);
        }else{
            BotUtils.log(LogLevel.ERROR,"Impossible de créer le fichier de `CommandWatcher` ("+logFile.getName()+")", true, false);
        }
        logWriter=  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
        BotUtils.log(LogLevel.INFO,"Démarrage des threads nécessaires...", true, true);
        consoleRunnable = new ConsoleRunnable();
        pool.execute(consoleRunnable);
        satellite = new Satellite(startTime);
        pool.execute(satellite);
        vulcain = new VulcainServer(44444);
        pool.execute(vulcain);
        BotUtils.log(LogLevel.INFO ,"Serveur Vulcain démarré à l'adresse "+vulcain.getAddress().getAddress().getHostAddress()+"/"+vulcain.getPort(), true, true);



    }

    public static Runnable getSatelliteRunnable() {
        return satellite;
    }

    public static Runnable getConsoleRunnable() {
        return consoleRunnable;
    }

    public static Runnable getVulcainRunnable() {
        return vulcain;
    }

    public static Properties getProperties() {
        return props;
    }

    public static File getLogFile() {
        return logFile;
    }

    public static Writer getLogWriter() {
        return logWriter;
    }

    public static long getStartTime() {return startTime;}

    public static WebSocketServer getServer() {
        return vulcain;
    }

    public static ExecutorService getThreadPool() {
        return pool;
    }
}
