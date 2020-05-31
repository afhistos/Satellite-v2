package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.ConsoleThread;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.database.SQLUtils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class StartHandler {
    private static long startTime;
    private static Properties props = new Properties();
    private static File logFile;
    private static Writer logWriter = null;
    private static Thread mainThread, consoleThread;

    public static void main(String[] args) throws IOException, LoginException, InterruptedException, SQLException {
        startTime = System.currentTimeMillis();
        props.load(StartHandler.class.getResourceAsStream("/props.properties"));
        BotUtils.log(LogLevel.INFO, "Démarrage de Satellite v"+props.getProperty("version"), true, false);
        logFile = new File("Satellite-log-"+BotUtils.getFullTimestamp(startTime)+".log");
        System.out.println(logFile.getName());
        if(logFile.createNewFile()){
            BotUtils.log(LogLevel.CONFIG, "Fichier de `CommandWatcher` ("+logFile.getName()+") créé!", true, false);
        }else{
            BotUtils.log(LogLevel.ERROR,"Impossible de créer le fichier de `CommandWatcher` ("+logFile.getName()+")", true, false);
        }
        logWriter=  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
        Satellite satellite = new Satellite(startTime, new EventWaiter());
        mainThread = new Thread(satellite, "Satellite-Thread");
        mainThread.start();
        consoleThread = new ConsoleThread();
        consoleThread.start();

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
}
