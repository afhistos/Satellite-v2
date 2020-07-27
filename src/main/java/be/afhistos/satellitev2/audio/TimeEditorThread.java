package be.afhistos.satellitev2.audio;

import be.afhistos.satellitev2.BotUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class TimeEditorThread extends Thread {
    Message message;
    AudioTrack track;
    EmbedBuilder embed;
    int songLength;
    int songPos;
    StringBuilder sb = new StringBuilder();

    public TimeEditorThread(Message msg, AudioTrack track, EmbedBuilder embed) {
        this.message = msg;
        this.track = track;
        this.embed = embed;
        songLength = Math.toIntExact(track.getDuration())/1000;//Durée de 'track' en secondes
        songPos = Math.toIntExact(track.getPosition())/1000;
        for (int i = 0; i < songLength/10; i++) {
            sb.append("○");
        }
        embed.setDescription(sb.toString());
        msg.editMessage(embed.build()).queue();
    }

    @Override
    public void run() {
        int i; //Nombre de bulles remplies
        int lastUpdate= 0;
        while(true) {
            songPos = Math.toIntExact(track.getPosition()) / 1000;
            if (songPos % 10 == 0) {
                i = (songPos / 10)-1;
                if(i >= 0){
                    if(lastUpdate > songPos){//La position de la musique est derrière la barre de progression
                        sb.replace(0,lastUpdate/10,"○");
                        System.out.println("lastUpdate : "+lastUpdate + "  songPos : "+songPos);
                    }
                    sb.replace(i, i+1, "●");
                }
                String formattedTime = BotUtils.getTimestamp(songPos * 1000, false) + "**/**" +
                        BotUtils.getTimestamp(songLength * 1000, false);
                embed.setDescription(sb.toString() + " " + formattedTime);
                message.editMessage(embed.build()).queue();
                lastUpdate = songPos;

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
