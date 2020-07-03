package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

//Test command
public class CommandSendToClient extends Command {

    public CommandSendToClient(){
        this.name = "send";
        this.ownerCommand= true;
        this.arguments = "Chaine à envoyer";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            event.reactError();
            return;
        }
        StartHandler.getServerInstance().send(event.getArgs());
    }
}
