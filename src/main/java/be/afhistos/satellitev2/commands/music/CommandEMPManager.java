package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;

public class CommandEMPManager extends CommandBase {
    public String TOPIC = "emp-channel-<guildID>\nCe salon héberge le lecteur multimédia. Afin de garantir son " +
            "fonctionnement, veuillez ne pas modifier son nom, sa description ni les permissions de Satellite.\n" +
            "Il aura besoin des permissions suivantes: Envoyer des embeds, des liens, éditer ses messages, " +
            "ajouter et gérer les réactions.<now>";

    public CommandEMPManager(){
        this.name = "EMPManager";
        this.aliases = new String[]{"embedplayer", "emp", "playermanager"};
        this.botRequiredPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userRequiredPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly =true;
        this.arguments = "help";
        this.help = "Commande-mère pour le Lecteur Multimédia Intégré";

    }


    @Override
    protected void execute(CommandEvent e) {
        if(e.getArgs().isEmpty()){//Aucun argument
            e.reactWarning();
            e.reply(getCommandHelp());
            return;
        }
        String[] sArgs = e.getArgs().split(" ");//Splitted arguments
        if(sArgs[0].equals("setup")){
            if(!e.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)){
                e.reactError();
                e.reply("J'ai besoin d'avoir la permission de gérer les salons pour créer et modifier celui du système EMP");
                return;
            }
            if(sArgs.length >= 2){
                TextChannel chan;
                try{
                    chan = e.getGuild().getTextChannelById(sArgs[1]);
                }catch (Exception ign){
                    e.getTextChannel().sendMessage("Aucun salon disponible avec l'id '"+sArgs[1]
                            +"'. Création d'un nouveau salon...").queue();
                    e.getGuild().createTextChannel("wait-for-setup").queue();
                    chan = e.getGuild().getTextChannelsByName("wait-for-setup", false).get(0);
                }
                setup(chan);
            }else{
                e.getGuild().createTextChannel("wait-for-setup").queue(this::setup);
            }
        }else if(sArgs[0].equals("delete")){

        }else if(sArgs[0].equals("clear")){
            AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild());//Permet de charger le lecteur si il n'existe pas
            TextChannel channel = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).embeddedPlayer.getChannel();
            int n = (sArgs.length >= 2 ? Integer.parseInt(sArgs[1]) : 100);
            if(n == 0){n++;}
            AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).embeddedPlayer.clearChannel(n);

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
                "   ┕ __setup <id>:__ comme la commande __setup__, mais la mise en place se fait dans le salon" +
                " textuel précisé via son identifiant." +
                "\n" +
                "__delete:__ permet de supprimer le système EMP automatiquement." + "\n" +
                "__clear <n>(1 » 100):__ permet de supprimer les <n> derniers messages du salon EMP. Si <n> n'est pas précisés, tous" +
                "les messages seront supprimés"+"\n"+
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