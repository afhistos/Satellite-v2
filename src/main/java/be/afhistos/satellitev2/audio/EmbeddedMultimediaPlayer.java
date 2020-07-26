package be.afhistos.satellitev2.audio;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.DefaultEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmbeddedMultimediaPlayer extends AudioEventAdapter {

    private final TextChannel channel;//Salon du lecteur
    private String id;//Id du message à modifier au fur et à mesure
    private GuildMusicManager musicManager;


    public EmbeddedMultimediaPlayer(TextChannel c){
        musicManager = AudioUtils.getInstance().getGuildAudioPlayer(c.getGuild());
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
        builder.setThumbnail("https://img.youtube.com/vi/"+t.getIdentifier()+"/hqdefault.jpg").setTitle(info.title, info.uri);
        builder.setDescription("Chargement...");
        builder.addField("Par "+info.author, "Volume: "+player.getVolume()+ "%    Basses: "
                +(musicManager.isBassActive() ? "":"dés")+
                "activées", false);
        builder.addField("Contrôles:",":arrow_up_small::arrow_down_small: : Gérer le volume\n" +
                ":arrow_backward::arrow_forward: : Passer à la musique précédente/suivante\n" +
                ":twisted_rightwards_arrows: : Mélanger la liste de lecture\n" +
                ":fast_forward: : Avancer de 10 secondes\n" +
                ":leftwards_arrow_with_hook: : Répéter la liste de lecture\n" +
                ":arrows_counterclockwise: : Répéter la musique en cours",false);
        builder.addField("Status:", (player.isPaused() ? "En pause" : "En cours de lecture"), true);
        builder.addField("Liste de lecture:", "[ **"+musicManager.scheduler.getQueue().size()+
                "** musiques restantes]-[ "+
                BotUtils.getTimestamp(musicManager.scheduler.getQueueDuration(),false), true);
        channel.retrieveMessageById(id).queue(message -> {
            message.editMessage(builder.build()).queue();
        });
    }
}
