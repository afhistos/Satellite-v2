package be.afhistos.satellitev2.audio;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildMusicManager {
    public final AudioPlayer player;
    //public final EmbeddedMultimediaPlayer embeddedPlayer;
    public final TrackScheduler scheduler;
    public final EqualizerFactory equalizer;
    private boolean bassActive;

    public GuildMusicManager(AudioPlayerManager manager){
        player = manager.createPlayer();
        equalizer = new EqualizerFactory();
        player.setFilterFactory(equalizer);
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        bassActive = false;
    }

    public AudioPlayerSendHandler getSendHandler(){
        return new AudioPlayerSendHandler(player);
    }


    public boolean isBassActive() {
        return bassActive;
    }

    public void setBassActive(boolean bassActive) {
        this.bassActive = bassActive;
    }
}
