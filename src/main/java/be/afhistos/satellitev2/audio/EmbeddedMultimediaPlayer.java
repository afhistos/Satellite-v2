package be.afhistos.satellitev2.audio;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.DefaultEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmbeddedMultimediaPlayer extends AudioEventAdapter {

    private final TextChannel channel;//Salon du lecteur
    private String id;//Id du message à modifier au fur et à mesure
    private GuildMusicManager musicManager;
    private Thread timeThread;

    public EmbeddedMultimediaPlayer(TextChannel c, GuildMusicManager musicManager){
        this.musicManager = musicManager;
        channel = c;
        EmbedBuilder builder = new DefaultEmbed(c.getJDA().getSelfUser());
        builder.setDescription("Le lecteur est prêt à l'emploi! Ajoutez une musique avec la commande `²play`.");
        channel.sendMessage(builder.build()).queue(message -> {
            id = message.getId();
        });
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack t) {
        EmbedBuilder builder = new DefaultEmbed(channel.getJDA().getSelfUser());
        AudioTrackInfo info = t.getInfo();
        builder.setThumbnail("https://img.youtube.com/vi/"+t.getIdentifier()+"/maxresdefault.jpg").setTitle(info.title, info.uri);
        builder.setDescription("Chargement...");
        builder.addField("Par "+info.author, "Volume: "+player.getVolume()+ "%    Basses: "
                +(musicManager.isBassActive() ? "":"dés")+
                "activées", false);
        builder.addField("Contrôles:",":arrow_backward::arrow_forward: : Passer à la musique précédente/suivante\n" +
                ":arrow_up_small::arrow_down_small: : Gérer le volume   " +
                ":twisted_rightwards_arrows: : Mélanger la liste de lecture\n" +
                ":fast_forward: : Avancer de 10 secondes" +
                ":leftwards_arrow_with_hook: : Répéter la liste de lecture\n" +
                ":arrows_counterclockwise: : Répéter la musique en cours " +
                ":loud_sound: : (Dés)activer les basses",false);
        builder.addField("Status:", (player.isPaused() ? "En pause" : "En cours de lecture"), true);
        if(channel.getGuild().getAudioManager().isConnected()){
            int n = channel.getGuild().getAudioManager().getConnectedChannel().getMembers().size() - 1;
            builder.addField("Auditeurs:", "Il y à **"+n+"** auditeur(s)", true);
        }
        builder.addField("Liste de lecture:", "[ **"+musicManager.scheduler.getQueue().size()+
                "** musiques restantes]", true);
        channel.retrieveMessageById(id).queue(message -> {
            message.editMessage(builder.build()).queue();
            timeThread = new TimeEditorThread(message,t,builder);
            timeThread.start();
            message.addReaction("\u25C0").queue();
            message.addReaction("\u25B6").queue();
            message.addReaction("\uD83D\uDD3C").queue();
            message.addReaction("\uD83D\uDD3D").queue();
            message.addReaction("\uD83D\uDD00").queue();
            message.addReaction("\u23E9").queue();
            message.addReaction("\u21A9").queue();
            message.addReaction("\uD83D\uDD04").queue();
            message.addReaction("\uD83D\uDD0A").queue();

        });

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        timeThread.interrupt();
        EmbedBuilder builder = new DefaultEmbed(channel.getJDA().getSelfUser());
        builder.setTitle("En attente");
        builder.setDescription("de la prochaine musique");
        builder.setThumbnail("http://afhistos.be/misc/googleSpinx64.gif");
        channel.retrieveMessageById(id).queue(message -> {
            message.editMessage(builder.build()).queue();
        });
    }

    public TextChannel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }
}
