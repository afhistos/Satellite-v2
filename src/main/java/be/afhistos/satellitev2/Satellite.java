package be.afhistos.satellitev2;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.*;
import be.afhistos.satellitev2.commands.music.*;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.consoleUtils.TextColor;
import be.afhistos.satellitev2.listeners.CommandWatcher;
import be.afhistos.satellitev2.listeners.DiscordEventListener;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.LinkedList;

public class Satellite implements Runnable{

    private static boolean running;
    private static final CommandClientBuilder builder = new CommandClientBuilder();
    private static CommandClient client;
    private static JDA bot;
    private long loadedTime;
    private final EventWaiter waiter;


    public Satellite(long st, EventWaiter waiter) throws LoginException, InterruptedException, SQLException {
        this.waiter = waiter;
        BotUtils.CAT_PERMISSIONS_DENY =  new LinkedList<>();
        BotUtils.CAT_PERMISSIONS_DENY.add(Permission.VIEW_CHANNEL);
        builder.setStatus(OnlineStatus.IDLE).setPrefix("²");
        builder.addCommands(new CommandMonitoring(), new CommandStopBot(), new CommandConfinement());
        builder.addCommands(new CommandBassBoost(), new CommandPlay(),new CommandVolume(),new CommandNowPlaying(),
                new CommandPlaylist(waiter), new CommandStopMusic(), new CommandSkip(), new CommandShuffle(),
                new CommandLoop(), new CommandJump(), new CommandPause(),new CommandClearPlaylist(), new CommandAutoRole());
        builder.setOwnerId("279597100961103872").setCoOwnerIds("225261996709380106");
        builder.setEmojis("\u2705", "\u26a0", "\u274c");
        builder.useHelpBuilder(true).setListener(new CommandWatcher());
        client = builder.build();
        JDABuilder botBuilder = JDABuilder.createLight(StartHandler.getProperties().getProperty("token"));
        EnumSet<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES
                , GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);
        EnumSet<CacheFlag> flags = EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);
        botBuilder.enableCache(flags);
        botBuilder.enableIntents(intents);
        botBuilder.addEventListeners(new DiscordEventListener(),client,this.waiter, new AudioUtils());
        botBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        bot = botBuilder.build().awaitReady();
        running = true;
        bot.getPresence().setStatus(OnlineStatus.ONLINE);
        bot.getPresence().setActivity(Activity.watching("Lego Ninjago"));
        loadedTime = System.currentTimeMillis();
        BotUtils.log(LogLevel.INFO, "Le bot est prêt à l'utilisation.\n"+TextColor.BRIGHT_CYAN+"Temps de chargement: "
                +TextColor.BRIGHT_BLUE+ BotUtils.getTimestamp(loadedTime - st, true), true, false);
    }

    @Override
    public void run() {
        while(true){
            if(isRunning()){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Thread t = new Thread(new ShutdownBot());
                t.setPriority(1);
                t.start();
            }
        }
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

    public static CommandClientBuilder getBuilder() {
        return builder;
    }

    public EventWaiter getWaiter() {
        return waiter;
    }
}
