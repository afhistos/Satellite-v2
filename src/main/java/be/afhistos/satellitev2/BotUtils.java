package be.afhistos.satellitev2;

import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.consoleUtils.TextColor;
import be.afhistos.satellitev2.database.QueryResult;
import net.dv8tion.jda.api.Permission;

import javax.management.*;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        String log = "("+ l.getKey()+") "+now+ l.getColor() + " VIA "+ ste.getFileName()+"#"+
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
        int sec = calendar.get(Calendar.SECOND);
        int min = calendar.get(Calendar.MINUTE);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int d= calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);
        return String.format("%02d-%02d-%02d_%02d'%02d'%02d", d,m,y,h,min,sec);
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
        return String.format("%02dh %02dm %02ds" +(showMs ? ".%sms": ""), h,min,sec,last_ms);
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

        /* Bandwidth usage
         * Since we cannot get bw usage via pure java, getting the values from a python program that runs on the hosting machine
         * And getting it through a database
         * Due to security reasons, the database isn't accessible from others machines.
         * So We first need to check if we can use database
         */
        sb.append("\nUtilisation de la bande passante: ");
        if(Satellite.useSQL()){
            String rawDl = null, rawUl = null;
            try{
                QueryResult qr = Satellite.getSqlInstance().execute("SELECT * FROM `general`");
                ResultSet r = qr.getResult();
                if(r.next()){
                    rawDl = r.getString("download");
                    rawUl = r.getString("upload");
                }
                qr.close();
            }catch (Exception e){e.printStackTrace();}
            if(rawDl.length() < 7){//Display the value in Kb
                dl = df.format(Double.parseDouble(rawDl) / 1024.0) + " Kbps";
            }else{//Display the value in Mb
                dl = df.format(Double.parseDouble(rawDl) / 1024.0 / 1024.0) + " Mbps";
            }
            if(rawUl.length() < 7){//Display the value in Ko
                ul = df.format(Double.parseDouble(rawUl) / 1024.0) + " Kbps";
            }else{
                ul = df.format(Double.parseDouble(rawUl) / 1024.0 / 1024.0) + " Mbps";
            }
            if(dl.startsWith(".")){dl = "0"+dl;}
            if(ul.startsWith(".")){ul = "0"+ul;}
            sb.append("↓").append(dl).append("   ↑").append(ul)
                    .append("\n __Notez que la mise à jour de la bande passante s'effectue toute les 15 secondes.__");
        }else{
            sb.append("Données indisponibles.");
        }
        return sb.toString();
    }

    public static Color getDefaultColor() {
        return new Color(50,50,175);
    }
}
