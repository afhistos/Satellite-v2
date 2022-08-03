package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandFetchApi extends SlashCommand {

    public CommandFetchApi(){
        this.name = "fetchapi";
        this.aliases = new String[]{"fetch", "sendRequest", "request"};
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "url", "L'adresse de l'API à fetch", true));
        this.options = data;
        this.category= new Category("Divers");
        this.help = "Permet d'envoyer une requete à l'adresse <URL> et en récupérer les données (JSON et XML uniquement)";
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String response;
        try {
            response = BotUtils.httpGet(event.getOption("url").getAsString(), BotUtils.isOwner(event.getMember()), true);
        } catch (IOException e) {
            event.reply(event.getClient().getError() + " Une erreur s'est produite lors de la récupération" +
                    " des données à __"+event.getOption("url").getAsString()+"__!");
            return;
        }
        if(response.equals("ErrNonAuthorized")){
            event.reply(event.getClient().getWarning() + " L'url donnée ne renvoie pas de données au format JSON/XML!").queue();
            return;
        }

        if(response.length() > 2000){
            try {
                Path tempPath = Files.createTempFile("Ganymede-fetch-"+event.getMember().getId()+"-", ".txt");
                File tempFile = tempPath.toFile();
                FileWriter writer = new FileWriter(tempFile);
                writer.write(response);
                writer.close();
                event.replyFile(tempFile, "response.txt").queue();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            event.reply(response).queue();
        }
    }
}
