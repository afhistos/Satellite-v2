package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.ConsoleThread;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.server.ServerThread;
import be.afhistos.satellitev2.server.VulcainServer;
import org.java_websocket.server.WebSocketServer;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class StartHandler {
    private static long startTime;
    private static Properties props = new Properties();
    private static File logFile;
    private static Writer logWriter = null;
    private static Thread mainThread, consoleThread, vulcainThread;

    private static WebSocketServer vulcain;


    public static void main(String[] args) throws IOException, LoginException, InterruptedException, SQLException {
        startTime = System.currentTimeMillis();
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
        consoleThread = new ConsoleThread();
        consoleThread.start();
        Satellite satellite = new Satellite(startTime);
        mainThread = new Thread(satellite, "Satellite-Thread");
        mainThread.start();
        vulcain = new VulcainServer(44444);
        vulcainThread = new ServerThread();
        vulcainThread.start();
        BotUtils.log(LogLevel.INFO ,"Serveur Vulcain démarré", true, true);



    }

    public static Thread getMainThread() {
        return mainThread;
    }

    public static Thread getConsoleThread() {
        return consoleThread;
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

    public static Thread getVulcainThread() {
        return vulcainThread;
    }
}
