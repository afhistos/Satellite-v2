package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.sql.QueryResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandStopBot extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("stopbot")){
            if(!BotUtils.isOwner(event.getMember())){
                event.reply(":warning: Seuls les propriétaires de serveurs peuvent utiliser cette commande.").queue();
                return;
            }
            Satellite.setRunning(false);
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("stopbot", "Permet de stopper le bot"));
        event.getGuild().updateCommands().addCommands(commandData).queue();

    }
}
