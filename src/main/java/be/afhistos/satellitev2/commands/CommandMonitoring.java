package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandMonitoring extends Command {

    public CommandMonitoring(){
        this.name = "monitoring";
        this.aliases = new String[]{"showcpu", "cpu", "monitor"};
        this.category= new Category("Divers");
        this.help = "Affiche l'utilisation des ressources utilisées par le bot. (Propriétaire)";
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent e) {
        e.reactSuccess();
        try {
            e.reply(BotUtils.getResourcesUsage());
        } catch (Exception exception) {
            e.getMessage().clearReactions().queue();
            e.reactError();
            e.reply("Impossible de récupérer l'utilisation des ressources allouées à Satellite");
            exception.printStackTrace();
        }
    }
}
