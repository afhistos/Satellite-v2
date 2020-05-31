package be.afhistos.satellitev2.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class CommandHelp extends Command {

    public CommandHelp(){
        this.name = "help";
        this.aliases = new String[]{"aide"};
        this.help = "Affiche toutes les informations disponibles à propos des commandes";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent e) {
        StringBuilder builder = new StringBuilder("Commandes de **" + e.getSelfUser().getName() + "**:\n");
        Iterator iterator = e.getClient().getCommands().iterator();
        HashMap<Category, ArrayList<Command>> map = new HashMap<>();
        Command command;
        while(iterator.hasNext()){
            command = (Command)iterator.next();
            if(!map.containsKey(command.getCategory())){
                map.put(command.getCategory(), null);
            }
        }
    }
}
