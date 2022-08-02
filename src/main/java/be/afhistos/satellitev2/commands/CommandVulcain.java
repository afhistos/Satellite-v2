package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.sql.QueryResult;
import be.afhistos.satellitev2.sql.SQLUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CommandVulcain extends ListenerAdapter {

    private SQLUtils utils;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("vulcain")){
            if(!BotUtils.isOwner(event.getMember())){
                event.reply(":warning: Seuls les propriétaires de serveurs peuvent utiliser cette commande.").queue();
                return;
            }
            QueryResult qr = utils.getData("SELECT id FROM auth WHERE user_id = ?", event.getUser().getId());
            boolean alreadyLinked = qr.getRowsCount() > 0;
            qr.close();
            if(event.getOptions().size() == 0){
                event.reply(getHelpMessage(alreadyLinked)).queue();
            }
            switch (event.getOption("action").getAsString()){
                case "new":
                    if(alreadyLinked){
                        utils.execute("DELETE FROM auth WHERE id = ?", qr.getValue(0,0));
                    }
                    UUID uuid = UUID.randomUUID();
                    utils.execute("INSERT INTO auth (user_id, token, is_admin) VALUES (?, ?, ?)",
                            event.getUser().getId(),
                            uuid.toString(),
                            BotUtils.isOwner(event.getUser().getId()));
                    event.getUser().openPrivateChannel().flatMap(channel ->
                                    channel.sendMessage("**Token**: "+ uuid +"\nCe message sera supprimé <t:"+ (Instant.now().getEpochSecond()+ 600)+":R>"))
                            .delay(Duration.ofMinutes(10))
                            .flatMap(Message::delete)
                            .queue();
                    break;
                case "show":{
                    if(alreadyLinked) {
                        qr = utils.getData("SELECT token FROM auth WHERE user_id = ?", event.getUser().getId());
                        final String retreivedUuid = qr.getValue(0,0);
                        event.getUser().openPrivateChannel().flatMap(channel ->
                                        channel.sendMessage("**Token**: "+ retreivedUuid +"\nCe message sera supprimé <t:"+ (Instant.now().getEpochSecond() + 600)+":R>"))
                                .delay(Duration.ofMinutes(10))
                                .flatMap(Message::delete)
                                .queue();
                    } else {
                        event.reply("Vous n'avez pas encore de token actif! `²vulcain new`").queue();
                    }
                    break;
                }
                case "delete":{
                    if(alreadyLinked) {
                        utils.execute("DELETE FROM auth WHERE user_id = ?", event.getUser().getId());
                        event.reply("Votre token à été supprimé.").queue();
                    } else {
                        event.reply("Vous n'avez pas encore de token actif! `²vulcain new`").queue();
                    }
                    break;
                }
                default:{
                    event.reply(getHelpMessage(alreadyLinked)).queue();
                }
            }
        }
    }


    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("vulcain", "Permet d'initialiser la mise en place de Vulcain").addOptions(new OptionData(OptionType.STRING, "action",  "new/show/delete", true)));
        event.getGuild().updateCommands().addCommands(commandData).queue();
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
