package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandGetLogs extends SlashCommand {

    public CommandGetLogs(){
        this.name = "getlogs";
        this.aliases = new String[]{"getLogs", "logs", "printLogs", "printlogs"};
        this.category= new Category("Divers");
        this.help = "Permet d'obtenir les dernieres logs";
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(StartHandler.getLogFile().exists()){
            event.replyFile(StartHandler.getLogFile(), StartHandler.getLogFile().getName())
                    .setEphemeral(true)
                    .queue();

        }else{
            event.reply("Le fichier des logs n'existe pas!").setEphemeral(true).queue();
        }
    }
}
