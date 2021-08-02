package be.afhistos.satellitev2.listeners;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.ConsoleListener;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.ChannelType;

public class CommandWatcher implements CommandListener, ConsoleListener {

    @Override
    public void onCommand(CommandEvent event, Command command) {
        if(event.isFromType(ChannelType.PRIVATE)){return;}
        boolean a = command ==null;
        boolean b = a || command.getCategory() == null;
        String s = event.getMember().getUser().getAsTag()+"("+event.getMember().getUser().getId()+") issued command: ["+
                (b? "Default":command.getCategory().getName())+"]"+event.getMessage().getContentDisplay();
        BotUtils.log(LogLevel.INFO, s,true, true);
    }

    @Override
    public void onCommandException(CommandEvent event, Command command, Throwable throwable) {
        if(event.isFromType(ChannelType.PRIVATE)){return;}
        boolean a = command ==null;
        boolean b = a || command.getCategory() == null;
        String s = event.getMember().getUser().getAsTag()+"("+event.getMember().getUser().getId()+") issued command: ["+
                (b? "Default":command.getCategory().getName())+"]"+event.getMessage().getContentDisplay() +
                " throwed an error: "+throwable.getMessage();
        event.reply("Une erreur est survenue lors du traitement de la commande :(\n```java\n"+throwable.getClass().getName()+"\n```");
        BotUtils.log(LogLevel.ERROR, s,true, true);
        throwable.printStackTrace();
    }

    public void onConsoleMessage() {

    }

    public void onConsoleCommand() {
    }
}
