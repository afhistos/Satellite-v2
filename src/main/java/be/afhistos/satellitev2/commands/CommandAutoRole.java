package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.StartHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class CommandAutoRole extends Command {

    public CommandAutoRole(){
        this.name = "setautorole";
        this.aliases = new String[]{"autorole", "setauto", "setar", "str"};
        this.arguments = "@Role";
        this.guildOnly = true;
        this.help = "Permet de définir le rôle à donner aux nouveaux membres.";
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            event.replyError("Merci de préciser le Rôle.");
            return;
        }
        StringBuilder roles = new StringBuilder();
        event.getMessage().getMentionedRoles().forEach(role -> {
            roles.append(role.getId()).append(" ");
        });
        String line = "AUTOROLE "+event.getGuild().getId()+" "+roles.toString();
        //Vérifier si le fichier contient déjà un autoRole pour ce serveur
        Scanner scanner;
        try {
            scanner = new Scanner(StartHandler.getDataFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            event.replyError("Impossible de sauvegarder ces données. Veuillez contacter mon concepteur.");
            return;
        }
        int lineNum = 0;
        while (scanner.hasNextLine()){
            String currentLine = scanner.nextLine();
            lineNum++;
            if(currentLine.startsWith("AUTOROLE +"event.getGuild().getId())){
                
            }
        }


    }
}
