package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandGetLogs extends Command {

    public CommandGetLogs(){
        this.name = "getlogs";
        this.aliases = new String[]{"getLogs", "logs", "printLogs", "printlogs"};
        this.category= new Category("Divers");
        this.help = "Permet d'obtenir les dernieres logs";
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(StartHandler.getLogFile().exists()){
            event.replyInDm("Voici les dernières logs:",StartHandler.getLogFile(), StartHandler.getLogFile().getName());

        }else{
            event.replyInDm("Le fichier des logs n'existe pas!");
        }
    }
}
