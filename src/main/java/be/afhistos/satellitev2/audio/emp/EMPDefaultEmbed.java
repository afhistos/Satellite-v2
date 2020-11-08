package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.DefaultEmbed;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class EMPDefaultEmbed extends EmbedBuilder {
    public EMPDefaultEmbed(AudioTrack t, GuildMusicManager musicManager, TextChannel channel){
        AudioTrackInfo info = t.getInfo();
        this.setThumbnail("https://img.youtube.com/vi/"+t.getIdentifier()+"/maxresdefault.jpg").setTitle(info.title, info.uri);
        this.setDescription("Chargement...");
        this.addField("Par "+info.author, "Volume: "+musicManager.player.getVolume()+ "%      Basses: "
                +(musicManager.isBassActive() ? "":"dés")+
                "activées", false);
        this.addField("Contrôles:",":arrow_backward::arrow_forward: : Passer à la musique précédente/suivante\n" +
                ":arrow_up_small::arrow_down_small: : Gérer le volume   " +
                ":twisted_rightwards_arrows: : Mélanger la liste de lecture\n" +
                ":fast_forward: : Avancer de 10 secondes" +
                ":play_pause: : Bouton play/pause\n"+
                ":leftwards_arrow_with_hook: : Répéter la liste de lecture " +
                ":arrows_counterclockwise: : Répéter la musique en cours\n" +
                ":loud_sound: : (Dés)activer les basses",false);
        this.addField("Status:", (musicManager.player.isPaused() ? "En pause" : "En cours de lecture"), true);
        this.setColor(new Color(151,60,170));
        if(channel.getGuild().getAudioManager().isConnected()){
            int n = channel.getGuild().getAudioManager().getConnectedChannel().getMembers().size() - 1;
            this.addField("Auditeurs:", "Il y à **"+n+"** auditeur(s)", true);
        }
        this.addField("Liste de lecture:", "[ **"+musicManager.scheduler.getQueue().size()+
                "** musiques restantes]", true);
    }
}
