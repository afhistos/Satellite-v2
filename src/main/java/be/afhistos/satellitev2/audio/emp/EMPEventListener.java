package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class EMPEventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if(!event.isFromGuild()){
            return;
        }
        if(event.getJDA().getSelfUser() == event.getUser()){
            return;
        }

        if(event.getChannel().getName().equals("sate-lecteur")){
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                if(message.getId().equals(AudioUtils.getInstance().getGuildAudioPlayer(event.getGuild())
                .embeddedPlayer.getId())){
                    if(message.getAuthor() == event.getJDA().getSelfUser()){
                        processReaction(event.getEmoji().asUnicode().getAsCodepoints().toUpperCase(), event.getGuild());
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }

                }
            });


        }
    }

    private void processReaction(String s, Guild g) {
        int vol;
        GuildMusicManager manager = AudioUtils.getInstance().getGuildAudioPlayer(g);
        switch (s){
            case "U+25C0": //Musique précédente
                manager.scheduler.playLastTrack();
                break;
            case "U+25B6": //Musique suivante
                manager.scheduler.skip();
                break;
            case "U+1F53C": //Volume haut
                vol = manager.player.getVolume() + 10;
                if(vol > 120){
                    vol = 120;
                }
                AudioUtils.getInstance().setVolume(g, vol);
                break;
            case "U+1F53D": //Volume bas
                vol = manager.player.getVolume() - 10;
                if(vol < 0){
                    vol = 0;
                }
                AudioUtils.getInstance().setVolume(g, vol);
                break;
            case "U+1F500": //Mélanger playlist
                manager.scheduler.shuffle();
                break;
            case "U+23E9": //Fast skip
                AudioTrack t = AudioUtils.getInstance().getPlayingTrack(g);
                t.setPosition(t.getPosition() + 10000);
                break;
            case "U+23EF": //play pause
                manager.player.setPaused(!manager.player.isPaused());
                break;
            case "U+21A9": //Répéter la playlist
                manager.scheduler.invertRepeatingPlaylist();
                break;
            case "U+1F504": //Répéter le morceau en cours
                manager.scheduler.invertRepeatingOnce();
                break;
            case "U+1F50A": //Switch bass
                manager.setBassActive(!manager.isBassActive());
                break;
        }
        AudioUtils.getInstance().getGuildAudioPlayer(g).embeddedPlayer.updateEmbedPlayer();
        AudioUtils.getInstance().getGuildAudioPlayer(g).embeddedPlayer.forceRender();
    }
}
