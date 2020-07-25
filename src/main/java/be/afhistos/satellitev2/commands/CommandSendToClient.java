package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

//Test command
public class CommandSendToClient extends Command {

    public CommandSendToClient(){
        this.name = "send";
        this.ownerCommand= true;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            event.reactError();
            return;
        }
        String toSend = event.getAuthor().getName()+ " : " + event.getArgs();
        if(toSend.length() > 40){
            toSend = toSend.substring(0,40);
        }
        StartHandler.getServerInstance().sendToClients("prntLcd "+toSend);
    }
}
