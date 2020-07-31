package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.BotUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class UpdateThread extends Thread {
    Message message;
    AudioTrack track;
    EmbedBuilder embed;
    int songLength;
    int songPos;

    public UpdateThread(Message msg, AudioTrack track, EmbedBuilder embed) {
        this.message = msg;
        this.track = track;
        this.embed = embed;
        songLength = Math.toIntExact(track.getDuration())/1000;//Durée de 'track' en secondes
        songPos = Math.toIntExact(track.getPosition())/1000;
        embed.setDescription(getProgressBar(songPos, songLength));
        msg.editMessage(embed.build()).queue();
    }

    @Override
    public void run() {
        while(true) {
            songPos = Math.toIntExact(track.getPosition()) / 1000;
            String formattedTime = BotUtils.getTimestamp(songPos * 1000, false) + "**/**" +
                    BotUtils.getTimestamp(songLength * 1000, false);
            embed.setDescription(getProgressBar(songPos, songLength) + " " + formattedTime);
            message.editMessage(embed.build()).queue();
            try {
                Thread.sleep(4500);
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
}
