package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandEMPManager extends SlashCommand {
    public String TOPIC = "emp-channel-<guildID>\nCe salon héberge le lecteur multimédia. Afin de garantir son " +
            "fonctionnement, veuillez ne pas modifier son nom, sa description ni les permissions de Satellite.\n" +
            "Il aura besoin des permissions suivantes: Envoyer des embeds, des liens, éditer ses messages, " +
            "ajouter et gérer les réactions.<now>";

    public CommandEMPManager(){
        this.name = "empmanager";
        this.aliases = new String[]{"embedplayer", "emp", "playermanager"};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly =true;
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "action", "L'action à éxécuter. \"help\" pour une liste des actions disponibles "));
        this.options = data;
        this.help = "Commande-mère pour le Lecteur Multimédia Intégré";
    }


    @Override
    protected void execute(SlashCommandEvent e) {
        if(e.getOptions().isEmpty() || e.getOption("action").getAsString().equals("help")){//Aucun argument
            e.reply(getCommandHelp()).setEphemeral(true).queue();
            return;
        }
        String action = e.getOption("action").getAsString();
        if(action.equals("setup")){
            if(!e.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)){
                e.reply("J'ai besoin d'avoir la permission de gérer les salons pour créer et modifier celui du système EMP").setEphemeral(true).queue();
                return;
            }

            e.getGuild().createTextChannel("wait-for-setup").queue(this::setup);
        }else if(action.equals("delete")){
            //A finir un jour
        }

    }

    private String getCommandHelp() {
        String sb = "\\*\\*\\*** Menu d'aide de la commande 'EMPManager' **\\*\\*\\*\n" + this.help + "\n" +
                "Alias : " + String.join(", ", this.aliases) + "\n" +
                "         __**Arguments**__" + "\n" +
                "__setup:__ permet de mettre en place le système EMP automatiquement. " +
                "*NB: Satellite devra arrêter la musique en cours et quitter le salon si il joue de la musique lors " +
                "de l'éxécution de la commande.*" +
                "\n" +
                "__delete:__ permet de supprimer le système EMP automatiquement." + "\n" +
                "__help:__ Affiche ce message." + "\n" +
                "Après l'éxécution de la commande __setup__, merci de lire la description du salon textuel créé/modifié," +
                " elle contient quelques informations complémentaires" + "\n" +
                "Satellite, toujours là pour vous servir • " + BotUtils.getFullTimestamp(System.currentTimeMillis());
        return sb;
    }
    private void setup(TextChannel c){
        c.sendTyping().queue();
        c.getManager().setNSFW(false).setName("sate-lecteur").setTopic((TOPIC.replace("<guildID>",c.getGuild().getId())
                .replace("<now>", Instant.now().toString()))).queue();
        //restart audio system
        AudioUtils.getInstance().stopMusic(c.getGuild());
        c.sendMessage("configuration terminée.").queue();
    }
}