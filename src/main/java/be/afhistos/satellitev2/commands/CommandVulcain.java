package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.sql.QueryResult;
import be.afhistos.satellitev2.sql.SQLUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CommandVulcain extends Command {

    private SQLUtils utils;

    public CommandVulcain(SQLUtils utils){
        this.utils = utils;
        this.name = "vulcain";
        this.arguments = "`rien`/new/show/delete";
        this.category= new Category("Divers");
        this.help = "Permet d'initialiser la mise en place de Vulcain";
        this.ownerCommand = false;
        this.guildOnly = false;
        this.botMissingPermMessage = "Ganymède doit pouvoir vous envoyer un message privé pour utiliser cette commande!";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!BotUtils.isOwner(event.getMember())){
            event.replyInDm(":warning: Seuls les propriétaires de serveurs peuvent utiliser cette commande.");
            return;
        }
        QueryResult qr = utils.getData("SELECT id FROM auth WHERE user_id = ?", event.getAuthor().getId());
        boolean alreadyLinked = qr.getRowsCount() > 0;
        qr.close();
        if(event.getArgs().isEmpty()){
            event.replyInDm(getHelpMessage(alreadyLinked));
        }
        switch (event.getArgs()){
            case "new":
                if(alreadyLinked){
                    utils.execute("DELETE FROM auth WHERE id = ?", qr.getValue(0,0));
                }
                UUID uuid = UUID.randomUUID();
                utils.execute("INSERT INTO auth (user_id, token, is_admin) VALUES (?, ?, ?)",
                        event.getAuthor().getId(),
                        uuid.toString(),
                        BotUtils.isOwner(event.getAuthor().getId()));
                event.getAuthor().openPrivateChannel().flatMap(channel ->
                    channel.sendMessage("**Token**: "+ uuid +"\nCe message sera supprimé <t:"+ (Instant.now().getEpochSecond()+ 600)+":R>"))
                            .delay(Duration.ofMinutes(10))
                            .flatMap(Message::delete)
                            .queue();
                break;
            case "show":{
                if(!alreadyLinked){
                    event.replyInDm("Vous n'avez pas encore de token actif! `²vulcain new`");
                }else{
                    qr = utils.getData("SELECT token FROM auth WHERE user_id = ?", event.getAuthor().getId());
                    final String retreivedUuid = qr.getValue(0,0);
                    event.getAuthor().openPrivateChannel().flatMap(channel ->
                                    channel.sendMessage("**Token**: "+ retreivedUuid +"\nCe message sera supprimé <t:"+ (Instant.now().getEpochSecond() + 600)+":R>"))
                            .delay(Duration.ofMinutes(10))
                            .flatMap(Message::delete)
                            .queue();
                }
                break;
            }
            case "delete":{
                if(!alreadyLinked){
                    event.replyInDm("Vous n'avez pas encore de token actif! `²vulcain new`");
                }else{
                    utils.execute("DELETE FROM auth WHERE user_id = ?", event.getAuthor().getId());
                    event.replyInDm("Votre token à été supprimé.");
                }
                break;
            }
            default:{
                event.replyInDm(getHelpMessage(alreadyLinked));
            }
        }
    }

    private String getHelpMessage(boolean alreadyLinked) {
        if(alreadyLinked){
            return "**Récapitulatif**\n"+
                    "Votre compte est déjà lié à Vulcain.\n"+
                    "»Vous souhaitez générer un nouveau token ? `²vulcain new` (l'ancien sera désactivé)\n" +
                    "»Vous souhaitez re-voir votre token ? `²vulcain show`\n" +
                    "»Vous souhaitez supprimer votre token ? `²vulcain delete`\n" +
                    "**COMING SOON**: Possibilité d'ajouter des collaborateurs.\n" +
                    "__**RAPPEL: NE PARTAGEZ JAMAIS VOTRE TOKEN A QUI QUE CE SOIT! Si vous pensez que quelqu'un vous l'a dérobé," +
                    "`²vulcain delete`.**__";
        }else{
            return "**Récapitulatif**\n" +
                    "Votre compte n'est pas lié à Vulcain.\n" +
                    "Vulcain est un logiciel qui permet de contrôler Ganymède, ainsi que modérer votre serveur discord" +
                    " à distance, sur votre smartphone/tablette/ordinateur. Une présentation est disponible ici: " +
                    "http://164.68.101.190/Vulcain\n" +
                    "»Convaincu ? `²vulcain new` pour commencer !\n" +
                    "»Vous n'êtes pas emballé ? Ce n'est pas grave! Ganymède fonctionne très bien sans lui, et seul le propriétaire d'un serveur" +
                    " peut activer et utiliser Vulcain.";
        }
    }


}
