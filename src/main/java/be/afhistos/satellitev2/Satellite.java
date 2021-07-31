package be.afhistos.satellitev2;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.emp.EMPEventListener;
import be.afhistos.satellitev2.commands.*;
import be.afhistos.satellitev2.commands.handler.CommandHandler;
import be.afhistos.satellitev2.commands.music.*;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import be.afhistos.satellitev2.consoleUtils.TextColor;
import be.afhistos.satellitev2.listeners.CommandWatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
    private static JDA bot;
    private static CommandHandler handler = new CommandHandler();
    private long loadedTime;

    public Satellite(long st) throws LoginException, InterruptedException, SQLException {
        BotUtils.CAT_PERMISSIONS_DENY =  new LinkedList<>();
        BotUtils.CAT_PERMISSIONS_DENY.add(Permission.VIEW_CHANNEL);
        handler.setPrefix("²");
        handler.addCommands(new CommandMonitoring(), new CommandStopBot(), new CommandConfinement(), new CommandEval());
        handler.addCommands(new CommandBassBoost(), new CommandPlay(),new CommandVolume(),new CommandNowPlaying(),
                new CommandPlaylist(), new CommandStopMusic(), new CommandSkip(), new CommandShuffle(),
                new CommandLoop(), new CommandJump(), new CommandPause(),new CommandClearPlaylist(),
                new CommandEMPManager(), new CommandHelp());
        handler.setOwnerId("279597100961103872").addCoOwnerIds("378598433314963467");
        handler.setEmojis("\u2705", "\u26a0", "\u274c");
        handler.setListener(new CommandWatcher());
        JDABuilder botBuilder = JDABuilder.createLight(StartHandler.getProperties().getProperty("token"));
        EnumSet<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES
                , GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);
        EnumSet<CacheFlag> flags = EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);
        botBuilder.enableCache(flags);
        botBuilder.enableIntents(intents);
        botBuilder.addEventListeners(new EMPEventListener(),handler, new AudioUtils());
        botBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
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
        Thread t = new Thread(new ShutdownBot(), "Thread-shutdown");
        t.start();
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Satellite.running = running;
    }

    public static CommandHandler getHandler() {
        return handler;
    }

    public static JDA getBot() {
        return bot;
    }
}
