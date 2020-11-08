package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.Satellite;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.reflect.Array;
import java.util.*;

public class CommandHelp extends Command {
    HashMap<Category, ArrayList<Command>> map = new HashMap<>();
    private boolean loaded = false;

    public CommandHelp(){
        this.name = "help";
        this.aliases = new String[]{"aide"};
        this.help = "Affiche toutes les informations disponibles à propos des commandes";
        this.guildOnly = false;

    }

    @Override
    protected void execute(CommandEvent e) {
        if(!loaded){
            load();
            loaded= true;
        }
        String pf = e.getClient().getPrefix();
        if(e.getArgs().isEmpty()){
            StringBuilder builder = new StringBuilder("Commandes de **" + e.getSelfUser().getName() + "**:\n");
            for(Map.Entry<Category, ArrayList<Command>> entry : map.entrySet()){
                Category key = entry.getKey();
                ArrayList<Command> value = entry.getValue();
                builder.append("\n »  __**").append(key.getName()).append("**__:").append("\n\n");
                for(Command c : value){
                    if(c.isHidden() && (!e.isOwner())){
                        continue;
                    }
                    if(c.isOwnerCommand()){builder.append(":crown:");}
                    builder.append("`").append(c.getName()).append(" ").append((c.getArguments() == null ? "" : c.getArguments())).append("`: ").append(c.getHelp()).append("\n");
                }
            }
            builder.append("Pour plus d'informations, contacter **afhistos**#*7905*");
            e.replyInDm(builder.toString());
        }else{
            boolean found = false;
            EmbedBuilder builder = new EmbedBuilder();
            for(Command c : Satellite.getClient().getCommands()){
                if(c.getName().equalsIgnoreCase(e.getArgs())&& (c.isHidden() && (!e.isOwner()))) {
                    found = true;
                    builder.setTitle("Informations de la commande __" + c.getName() + "__");
                    builder.setDescription(c.getHelp());
                    builder.addField("Alias:", String.join(", ", c.getAliases()), false);
                    builder.addBlankField(true);
                    builder.addBlankField(true);
                    builder.addField("Tags:",
                            (c.isHidden() ? ":white_check_mark:" : ":negative_squared_cross_mark:") + "Cachée\n" +
                                    (c.isOwnerCommand() ? ":white_check_mark:" : ":negative_squared_cross_mark:") + "Administrateur" +
                                    (c.isGuildOnly() ? ":white_check_mark:" : ":negative_squared_cross_mark:") + "Serveur", true);
                    break;
                }
            }
            if(!found){
                builder.setTitle("Aucune commande n'est disponible sous le nom `"+e.getArgs()+"`");
                builder.setDescription("Tu peux voir toutes les commandes disponibles avec "
                        +e.getClient().getPrefix()+ name);
            }
            e.replyInDm(builder.build());
        }

    }

    //load the map only at load, the commands will not be modified after start
    private void load(){
        Iterator iterator = Satellite.getClient().getCommands().iterator();
        Command command;
        while(iterator.hasNext()){
            command = (Command)iterator.next();
            Category cat = command.getCategory();
            if(cat == null){
                cat = new Category("Commandes en test");
            }
            addToList(cat, command);
        }
    }

    //Code adapted from https://stackoverflow.com/a/12134901
    private synchronized void addToList(Category mapKey, Command myItem) {
        ArrayList<Command> itemsList = map.get(mapKey);
        // if list does not exist create it
        if(itemsList == null) {
            itemsList = new ArrayList<Command>();
            itemsList.add(myItem);
            map.put(mapKey, itemsList);
        } else {
            // add if item is not already in list
            if(!itemsList.contains(myItem)) itemsList.add(myItem);
        }
    }
}
