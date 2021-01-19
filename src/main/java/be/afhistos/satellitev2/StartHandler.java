package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.ConsoleThread;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.server.ServerThread;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class StartHandler {
    private static long startTime;
    private static Properties props = new Properties();
    private static File logFile, dataFile;
    private static Writer logWriter = null, dataWriter = null;
    private static Thread mainThread, consoleThread;
    private static ServerThread serverThread;

    public static int SERVER_PORT = 4444;


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
        BotUtils.log(LogLevel.INFO,"Chargement du fichier de données...", true ,true);
        dataFile = new File("Satellite-data.txt");
        if(!dataFile.exists()){
            if(dataFile.createNewFile()){
                BotUtils.log(LogLevel.CONFIG, "Fichier de données ("+dataFile.getName()+") créé!", true,false);
            }else{
                BotUtils.log(LogLevel.ERROR, "Impossible de créer le fichier de données ("+dataFile.getName()+")", true, true);
            }
        }
        dataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile), "UTF-8"));
        BotUtils.log(LogLevel.INFO,"Démarrage des threads nécessaires...", true, true);
        consoleThread = new ConsoleThread();
        consoleThread.start();
        Satellite satellite = new Satellite(startTime, new EventWaiter());
        mainThread = new Thread(satellite, "Satellite-Thread");
        mainThread.start();
        BotUtils.log(LogLevel.INFO,"Démarrage du serveur Vulcain...", true, true);
        serverThread = new ServerThread(SERVER_PORT);
        serverThread.start();


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

    public static File getDataFile() {
        return dataFile;
    }

    public static Writer getDataWriter() {
        return dataWriter;
    }

    public static ServerThread getServerThread() {
        return serverThread;
    }

    public static void setServerThread(ServerThread server) {
        serverThread = server;
    }
}
