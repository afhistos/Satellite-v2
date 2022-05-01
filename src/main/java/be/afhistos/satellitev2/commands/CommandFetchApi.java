package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import net.dv8tion.jda.api.JDAInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandFetchApi extends Command {

    public CommandFetchApi(){
        this.name = "fetchApi";
        this.aliases = new String[]{"fetch", "sendRequest", "request"};
        this.arguments = "<URL>";
        this.category= new Category("Divers");
        this.help = "Permet d'envoyer une requete à l'adresse <URL> et en récupérer les données (JSON et XML uniquement)";
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            event.replyWarning("Aucune adresse n'a été spécifiée!");
            return;
        }
        String response;
        try {
            response = BotUtils.httpGet(event.getArgs(), BotUtils.isOwner(event.getMember()), true);
        } catch (IOException e) {
            event.replyError("Une erreur s'est produite lors de la récupération des données à __"+event.getArgs()+"__!");
            return;
        }
        if(response.equals("ErrNonAuthorized")){
            event.replyWarning("L'url donnée ne renvoie pas de données au format JSON/XML!");
            return;
        }

        if(response.length() > 2000){
            try {
                Path tempPath = Files.createTempFile("Ganymede-fetch-"+event.getAuthor().getId()+"-", ".txt");
                File tempFile = tempPath.toFile();
                FileWriter writer = new FileWriter(tempFile);
                writer.write(response);
                writer.close();
                event.reply(tempFile, "response.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            event.reply(response);
        }
    }
}
