package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.DefaultEmbed;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmbeddedMultimediaPlayer extends AudioEventAdapter {

    private final TextChannel channel;//Salon du lecteur
    private String id;//Id du message à modifier au fur et à mesure
    private GuildMusicManager musicManager;
    private UpdateThread updateThread;

    public EmbeddedMultimediaPlayer(TextChannel c, GuildMusicManager musicManager){
        channel = c;
        this.musicManager = musicManager;
        EmbedBuilder builder = new DefaultEmbed(c.getJDA().getSelfUser());
        builder.setDescription("Le lecteur est prêt à l'emploi! Ajoutez une musique avec la commande `²play`.");
        channel.sendMessageEmbeds(builder.build()).queue(message -> {
            id = message.getId();
            System.out.println("ID: "+ id);
            clearChannel(100);
        });

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack t) {
        if(updateThread == null){
            EmbedBuilder builder = new EMPDefaultEmbed(t, musicManager, channel);
            channel.retrieveMessageById(id).queue(message -> {
                message.editMessageEmbeds(builder.build()).queue();
                updateThread = new UpdateThread(message,builder, t, musicManager);
                updateThread.start();
                message.addReaction(Emoji.fromUnicode("\u25C0")).queue();
                message.addReaction(Emoji.fromUnicode("\u25B6")).queue();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDD3C")).queue();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDD3D")).queue();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDD00")).queue();
                message.addReaction(Emoji.fromUnicode("\u23E9")).queue();
                message.addReaction(Emoji.fromUnicode("\u23EF")).queue();
                message.addReaction(Emoji.fromUnicode("\u21A9")).queue();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDD04")).queue();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDD0A")).queue();
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
            message.editMessageEmbeds(builder.build()).queue();
        });
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        updateEmbedPlayer();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        updateEmbedPlayer();
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
                if(msg.getId().equals(id)){
                    System.out.println("Found the NoDelId.");
                }else{
                    System.out.println("Msg found: "+msg.getId()+" || NoDelId: "+id);
                    msg.delete().queue();
                }
            }
        });
    }

    public void updateEmbedPlayer(){
        updateThread.setEmbed(new EMPDefaultEmbed(updateThread.getTrack(), musicManager, channel));
    }

    public void forceRender(){
        updateThread.forceRender();
    }
}
