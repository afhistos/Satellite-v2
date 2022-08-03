package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandMonitoring extends SlashCommand {

    public CommandMonitoring(){
        this.name = "monitoring";
        this.aliases = new String[]{"showcpu", "cpu", "monitor"};
        this.category= new Category("Divers");
        this.help = "Affiche l'utilisation des ressources utilisées par le bot. (Propriétaire)";
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        try {
            e.reply(BotUtils.getResourcesUsage()).setEphemeral(true).queue();
        } catch (Exception exception) {
            e.reply("Impossible de récupérer l'utilisation des ressources allouées à Satellite").setEphemeral(true).queue();
            exception.printStackTrace();
        }
    }
}
