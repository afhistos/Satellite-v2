package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.consoleUtils.TextColor;
import be.afhistos.satellitev2.server.ServerThread;
import net.dv8tion.jda.api.Permission;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

public class BotUtils {
    public static String WHITE_CHECK_MARK = "\\u2705";
    public static String CROSS_MARK = "\\u274c";
    public static String DEFAULT_CAT_NAME = "Satellite-Utils";
    public static String LOGS_CHANNEL_NAME = "logs";
    public static String LMI_CHANNEL_NAME = "audio-player";
    public static Collection<Permission> CAT_PERMISSIONS_DENY;

    private static DecimalFormat df = new DecimalFormat("#.00");

    public static void log(LogLevel l, String s, boolean logToConsole, boolean logToFile){
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String now = getFullTimestamp(System.currentTimeMillis());
        String log = "("+ l.getKey()+") "+now+ l.getColor() + " VIA "+ ste.getFileName().replace(".java","")+"#"+
                ste.getMethodName()+":"+ste.getLineNumber()+" :: "+s;
        if(logToConsole){System.out.println(log+ TextColor.RESET);}
        if(logToFile){
            if(StartHandler.getLogFile().exists()){
                try{
                    for (int i = 0; i < TextColor.FOREGROUNDS.length; i++) {
                        log = log.replace(TextColor.FOREGROUNDS[i], "");
                    }
                    for (int i = 0; i < TextColor.BACKGROUNDS.length; i++) {
                        log = log.replace(TextColor.BACKGROUNDS[i], "");
                    }
                    StartHandler.getLogWriter().append(log).append("\n").flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    log(LogLevel.SYSTEM, "IMPOSSIBLE D'ÉCRIRE DANS LE FICHIER DE LOGS!", true, false);
                }
            }else{
                log(LogLevel.SYSTEM, "LE FICHIER DE LOGS N'EXISTE PAS !", true, false);
            }
        }
    }

    public static String getFullTimestamp(long ms){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        calendar.setTimeInMillis(ms);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy_hh-mm-ss");
        return sdf.format(calendar.getTime());
    }

    public static String getFullDate(long ms){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        calendar.setTimeInMillis(ms);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        return sdf.format(calendar.getTime());


    }

    public static String getTimestamp(long ms, boolean showMs) {
       String last_ms = "";
        if(showMs){
           last_ms = String.valueOf(ms);
           if(last_ms.length() > 3){
               last_ms = last_ms.substring(last_ms.length() - 3);

           }
       }
        int sec = (int) (ms /1000)%60;
        int min = (int) (ms /60000)%60;
        int h = (int) ((ms /(1000 * 60 *60)) %24);
        String hours = "";
        if(h >0){
            hours = String.format("%02dh", h);
        }
        return String.format(hours + "%02dm %02ds" +(showMs ? ".%sms": ""), min,sec,last_ms);
    }
    public static String getResourcesUsage() throws Exception {
        StringBuilder sb = new StringBuilder("Utilisation des ressources par Satellite:");
        String dl, ul;
        //CPU load
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
        if(list.isEmpty()){sb.append("0.0");}
        else{
            Attribute att = (Attribute)list.get(0);
            Double value = (Double) att.getValue();
            //Cela prends un certain temps avant de récupérer les vraies valeurs
            if(value == -1.0) {sb.append("0.0");}
            else{
                sb.append((int)(value * 1000) / 10.0);
            }
        }
        sb.append("%\n").append("\\*\\*\\*** Statistiques d'utilisation de la RAM **\\*\\*\\*\n");
        //RAM Usage
        int mb = 1024*1024;
        Runtime run = Runtime.getRuntime();
        sb.append("Mémoire totale: ").append(run.totalMemory() / mb).append(" Mb\n");
        sb.append("Mémoire disponible: ").append(run.freeMemory() / mb).append(" Mb\n");
        sb.append("Mémoire utilisée: ").append((run.totalMemory() - run.freeMemory()) / mb).append(" Mb\n");
        sb.append("Mémoire maximale: ").append(run.maxMemory() / mb).append(" Mb\n");
        return sb.toString();
    }
    public static Color getDefaultColor() {
        return new Color(50,50,175);
    }

    public static void stopVulcain(){
        BotUtils.log(LogLevel.WARNING, "Arrêt du serveur Vulcain...", true, true);
        StartHandler.getServerThread().interrupt();
    }

    public static void startVulcain(){
        BotUtils.log(LogLevel.WARNING, "Démarrage du serveur Vulcain...", true, true);
        StartHandler.setServerThread(new ServerThread(StartHandler.SERVER_PORT));
    }

    public static void restartVulcain(){
        BotUtils.log(LogLevel.WARNING, "Redémarrage du serveur Vulcain...", true, true);
        stopVulcain();
        startVulcain();
    }
    public static String getIpAddress(Socket s){
        return ((InetSocketAddress)s.getRemoteSocketAddress()).getAddress().getAddress().toString();
    }
}
