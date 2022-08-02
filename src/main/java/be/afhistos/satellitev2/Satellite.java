package be.afhistos.satellitev2;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.emp.EMPEventListener;
import be.afhistos.satellitev2.commands.*;
import be.afhistos.satellitev2.commands.music.*;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.consoleUtils.TextColor;
import be.afhistos.satellitev2.listeners.CommandWatcher;
import be.afhistos.satellitev2.sql.SQLUtils;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

public class Satellite implements Runnable{

    private static boolean running;
    private static JDA bot;
    private static EventWaiter waiter;
    private static CommandClientBuilder builder = new CommandClientBuilder();
    private static CommandClient client;
    private static SQLUtils utils;
    private long loadedTime;

    public Satellite(long st) throws LoginException, InterruptedException, SQLException {
        BotUtils.CAT_PERMISSIONS_DENY =  new LinkedList<>();
        BotUtils.CAT_PERMISSIONS_DENY.add(Permission.VIEW_CHANNEL);
        utils = new SQLUtils(true);
        waiter = new EventWaiter();
        builder.setPrefix("&");
        builder.addCommands( new CommandConfinement());
        builder.addCommands(new CommandBassBoost(), new CommandPlay(),new CommandVolume(),new CommandNowPlaying(),
                new CommandPlaylist(waiter), new CommandStopMusic(), new CommandSkip(), new CommandShuffle(),
                new CommandLoop(), new CommandJump(), new CommandPause(),new CommandClearPlaylist(),
                new CommandEMPManager());
        builder.setOwnerId("279597100961103872").setCoOwnerIds("378598433314963467");
        builder.setEmojis("\u2705", "\u26a0", "\u274c");
        builder.setListener(new CommandWatcher());

        client = builder.build();
        JDABuilder botBuilder = JDABuilder.createLight(StartHandler.getProperties().getProperty("token"));
        EnumSet<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES
                , GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES
                , GatewayIntent.MESSAGE_CONTENT);
        EnumSet<CacheFlag> flags = EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);
        botBuilder.enableCache(flags);
        botBuilder.enableIntents(intents);
        botBuilder.addEventListeners(new EMPEventListener(), client, new AudioUtils());
        botBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        botBuilder.addEventListeners(new CommandVulcain(),new CommandStopBot(),new CommandGetLogs(),new CommandFetchApi(),new CommandMonitoring());
        bot = botBuilder.build().awaitReady();
        running = true;
        bot.getPresence().setActivity(Activity.competing("Lego Ninjago"));
        loadedTime = System.currentTimeMillis();
        BotUtils.log(LogLevel.INFO, "Le bot est prêt à l'utilisation.\n"+ TextColor.BRIGHT_CYAN+"Temps de chargement: "
                +TextColor.BRIGHT_BLUE+ BotUtils.getTimestamp(loadedTime - st, true), true, false);
    }

    @Override
    public void run() {
        while(true){
            if (!isRunning()) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Calling ShutdownBot from Satellite class");
        Thread t = new Thread(new ShutdownBot(), "Thread-shutdown");
        t.start();
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Satellite.running = running;
    }

    public static CommandClient getClient() {
        return client;
    }

    public static JDA getBot() {
        return bot;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static SQLUtils getUtils() {
        return utils;
    }
}
