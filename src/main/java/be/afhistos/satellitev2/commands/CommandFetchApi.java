package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandFetchApi extends ListenerAdapter {



    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("fetchApi")){
            if(event.getOptions().size() == 0){
                event.reply("Aucune adresse n'a été spécifiée!").queue();
                return;
            }
            String response;
            try {
                response = BotUtils.httpGet(event.getOption("ip").getAsString(), BotUtils.isOwner(event.getMember()), true);
            } catch (IOException e) {
                event.reply("Une erreur s'est produite lors de la récupération des données à __"+event.getOption("ip")+"__!").queue();
                return;
            }
            if(response.equals("ErrNonAuthorized")){
                event.reply("L'url donnée ne renvoie pas de données au format JSON/XML!").queue();
                return;
            }

            if(response.length() > 2000){
                try {
                    Path tempPath = Files.createTempFile("Ganymede-fetch-"+event.getMember().getId()+"-", ".txt");
                    File tempFile = tempPath.toFile();
                    FileWriter writer = new FileWriter(tempFile);
                    writer.write(response);
                    writer.close();
                    event.replyFile(tempFile).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                event.reply(response);
            }
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("fetchApi", "Permet d'envoyer une requete à l'adresse <URL> et en récupérer les données (JSON et XML uniquement)").addOptions(new OptionData(OptionType.STRING, "URL", "L'adresse à laquelle envoyer la requête",true)));
        event.getGuild().updateCommands().addCommands(commandData).queue();

    }
}
