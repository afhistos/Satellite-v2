package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.sql.QueryResult;
import be.afhistos.satellitev2.sql.SQLUtils;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandVulcain extends SlashCommand {

    private SQLUtils utils;

    public CommandVulcain(SQLUtils utils){
        this.utils = utils;
        this.name = "vulcain";
        this.arguments = "`rien`/new/show/delete";
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "action", "Permet de gérer ses identifiants Vulcain", true));
        this.options = data;
        this.category= new Category("Divers");
        this.help = "Permet d'initialiser la mise en place de Vulcain";
        this.ownerCommand = false;
        this.guildOnly = false;
        this.botMissingPermMessage = "Ganymède doit pouvoir vous envoyer un message privé pour utiliser cette commande!";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String action = event.getOption("action") == null ? "" : event.getOption("action").getAsString();
        if(!BotUtils.isOwner(event.getMember())){
            event.reply(":warning: Seuls les propriétaires de serveurs peuvent utiliser cette commande.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        QueryResult qr = utils.getData("SELECT id FROM auth WHERE user_id = ?", event.getUser().getId());
        boolean alreadyLinked = qr.getRowsCount() > 0;
        qr.close();
        if(action.isEmpty()){
            event.reply(getHelpMessage(alreadyLinked)).setEphemeral(true).queue();
        }
        switch (action){
            case "new":
                if(alreadyLinked){
                    utils.execute("DELETE FROM auth WHERE id = ?", qr.getValue(0,0));
                }
                UUID uuid = UUID.randomUUID();
                utils.execute("INSERT INTO auth (user_id, token, is_admin) VALUES (?, ?, ?)",
                        event.getMember().getId(),
                        uuid.toString(),
                        BotUtils.isOwner(event.getMember().getId()));
                event.reply("**Token**: ||"+ uuid +"||\nGarde ce token secret!").setEphemeral(true).queue();
                break;
            case "show":{
                if(!alreadyLinked){
                    event.reply("Vous n'avez pas encore de token actif! `²vulcain new`").setEphemeral(true).queue();
                }else{
                    qr = utils.getData("SELECT token FROM auth WHERE user_id = ?", event.getUser().getId());
                    final String retreivedUuid = qr.getValue(0,0);
                    event.reply("**Token**: ||"+ retreivedUuid +"||\nGarde ce token secret!").setEphemeral(true).queue();

                }
                break;
            }
            case "delete":{
                if(!alreadyLinked){
                    event.reply("Vous n'avez pas encore de token actif! `²vulcain new`").setEphemeral(true).queue();
                }else{
                    utils.execute("DELETE FROM auth WHERE user_id = ?", event.getUser().getId());
                    event.reply("Votre token à été supprimé.").setEphemeral(true);
                }
                break;
            }
            default:{
                event.reply(getHelpMessage(alreadyLinked)).setEphemeral(true).queue();
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
