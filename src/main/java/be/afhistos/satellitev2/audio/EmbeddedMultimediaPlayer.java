package be.afhistos.satellitev2.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmbeddedMultimediaPlayer extends AudioEventAdapter {

    public EmbeddedMultimediaPlayer(){

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }
}
