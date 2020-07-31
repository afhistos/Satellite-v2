package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.DefaultEmbed;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmbeddedMultimediaPlayer extends AudioEventAdapter {

    private final TextChannel channel;//Salon du lecteur
    private String id;//Id du message à modifier au fur et à mesure
    private GuildMusicManager musicManager;
    private UpdateThread updateThread;

    public EmbeddedMultimediaPlayer(TextChannel c, GuildMusicManager musicManager){
        this.musicManager = musicManager;
        channel = c;
        clearChannel(100);
        EmbedBuilder builder = new DefaultEmbed(c.getJDA().getSelfUser());
        builder.setDescription("Le lecteur est prêt à l'emploi! Ajoutez une musique avec la commande `²play`.");
        channel.sendMessage(builder.build()).queue(message -> {
            id = message.getId();
        });
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack t) {
        if(updateThread == null){
            EmbedBuilder builder = new EMPDefaultEmbed(player, t, musicManager, channel);
            channel.retrieveMessageById(id).queue(message -> {
                message.editMessage(builder.build()).queue();
                updateThread = new UpdateThread(message,builder, t);
                updateThread.start();
                message.addReaction("\u25C0").queue();
                message.addReaction("\u25B6").queue();
                message.addReaction("\uD83D\uDD3C").queue();
                message.addReaction("\uD83D\uDD3D").queue();
                message.addReaction("\uD83D\uDD00").queue();
                message.addReaction("\u23E9").queue();
                message.addReaction("\u21A9").queue();
                message.addReaction("\u23EF").queue();
                message.addReaction("\uD83D\uDD04").queue();
                message.addReaction("\uD83D\uDDD1").queue();
                message.addReaction("\uD83D\uDD0A").queue();
            });
        }else{
            updateThread.setTrack(t);
        }

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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

    public void clearChannel(int limit){
        channel.getHistory().retrievePast(limit).queue(messages -> {
            for (Message msg : messages){
                if(msg.getId()==id){
                    System.out.println("Found the NoDelId.");
                }else{
                    msg.delete().queue();
                }
            }
        });
    }
}
