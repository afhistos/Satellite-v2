package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class UpdateThread extends Thread {
    private Message message;
    private AudioTrack track;
    private EmbedBuilder embed;
    private GuildMusicManager musicManager;
    private int songLength;
    private int songPos;

    public UpdateThread(Message msg, EmbedBuilder embed, AudioTrack t, GuildMusicManager musicManager) {
        this.message = msg;
        this.embed = embed;
        this.track = t;
        this.musicManager = musicManager;
        if(!t.getInfo().isStream){
            songPos = Math.toIntExact(track.getPosition())/1000;
            songLength = Math.toIntExact(track.getDuration())/1000;//Durée de 'track' en secondes
            embed.setDescription(getProgressBar(songPos, songLength));
        }else{
            embed.setDescription("C'est un stream");
        }
        msg.editMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void run() {
        while(true) {
            if(!track.getInfo().isStream){
                songPos = Math.toIntExact(track.getPosition()) / 1000;
                if(songPos %5 == 0){//Régule les mises à jours envoyées à discord
                    String formattedTime = BotUtils.getTimestamp(songPos * 1000, false) + "**/**" +
                            BotUtils.getTimestamp(songLength * 1000, false);
                    embed.setDescription(getProgressBar(songPos, songLength) + " " + formattedTime);
                    message.editMessageEmbeds(embed.build()).queue(msg ->{}, throwable -> {

                });
            }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    private String getProgressBar(int pos, int length){// en secondes
        StringBuilder s= new StringBuilder();
        for (int j = 0; j < pos/10; j++) {
            s.append("●");
        }
        for (int j = 0; j < ((length/10) - (pos/10)); j++) {
            s.append("○");
        }
        return s.toString();
    }

    public void setTrack(AudioTrack track) {
        this.track = track;
        embed.setThumbnail("https://img.youtube.com/vi/"+track.getIdentifier()+"/maxresdefault.jpg")
                .setTitle(track.getInfo().title, track.getInfo().uri);
        message.editMessageEmbeds(embed.build()).queue();
    }

    public AudioTrack getTrack() {
        return track;
    }

    public void setEmbed(EmbedBuilder embed) {
        this.embed = embed;
    }

    public void forceRender(){
        message.editMessageEmbeds(embed.build()).queue();
    }
}
