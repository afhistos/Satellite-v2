package be.afhistos.satellitev2.audio;

import be.afhistos.satellitev2.Satellite;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Optional;

public class GuildMusicManager {
    public final AudioPlayer player;
    private String guildId;
    public final EmbeddedMultimediaPlayer embeddedPlayer;
    public final TrackScheduler scheduler;
    public final EqualizerFactory equalizer;
    private boolean bassActive;

    public GuildMusicManager(AudioPlayerManager manager, String guildId){
        player = manager.createPlayer();
        this.guildId = guildId;
        equalizer = new EqualizerFactory();
        player.setFilterFactory(equalizer);
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        bassActive = false;
        if(hasEMP(manager)){
            embeddedPlayer = new EmbeddedMultimediaPlayer(AudioUtils.getInstance().retreiveEMP(Satellite.getBot().getGuildById(guildId)), this);
            player.addListener(embeddedPlayer);
        }else{
            embeddedPlayer = null;
        }
    }

    private boolean hasEMP(AudioPlayerManager manager) {
        return AudioUtils.getInstance().retreiveEMP(Satellite.getBot().getGuildById(guildId)) != null;
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
