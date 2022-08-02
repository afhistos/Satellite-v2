package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandMonitoring extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        String command = e.getName();
        if(command.equals("monitoring")){
            try {
                e.reply(BotUtils.getResourcesUsage()).queue();
            } catch (Exception exception) {
                e.reply("Impossible de récupérer l'utilisation des ressources allouées à Satellite").queue();
                exception.printStackTrace();
            }
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("monitoring", "Affiche l'utilisation des ressources utilisées par le bot. (Propriétaire)"));
        event.getGuild().updateCommands().addCommands(commandData).queue();

    }
}
