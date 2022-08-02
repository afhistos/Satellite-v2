package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandGetLogs extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("getlogs")){
            if(StartHandler.getLogFile().exists()){
                event.reply("Voici les dernières logs:"+StartHandler.getLogFile()+ StartHandler.getLogFile().getName()).queue();

            }else{
                event.reply("Le fichier des logs n'existe pas!").queue();
            }
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("getlogs", "Permet d'obtenir les dernieres logs"));
        event.getGuild().updateCommands().addCommands(commandData).queue();

    }
}
