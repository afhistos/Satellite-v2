package be.afhistos.satellitev2.audio.emp;

import be.afhistos.satellitev2.DefaultEmbed;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class EMPDefaultEmbed extends EmbedBuilder {
    public EMPDefaultEmbed(AudioPlayer player, AudioTrack t, GuildMusicManager musicManager, TextChannel channel){
        EmbedBuilder builder =  new DefaultEmbed(Satellite.getBot().getSelfUser());
        AudioTrackInfo info = t.getInfo();
        builder.setThumbnail("https://img.youtube.com/vi/"+t.getIdentifier()+"/maxresdefault.jpg").setTitle(info.title, info.uri);
        builder.setDescription("Chargement...");
        builder.addField("Par "+info.author, "Volume: "+player.getVolume()+ "%      Basses: "
                +(musicManager.isBassActive() ? "":"dés")+
                "activées", false);
        builder.addField("Contrôles:",":arrow_backward::arrow_forward: : Passer à la musique précédente/suivante\n" +
                ":arrow_up_small::arrow_down_small: : Gérer le volume   " +
                ":twisted_rightwards_arrows: : Mélanger la liste de lecture\n" +
                ":fast_forward: : Avancer de 10 secondes" +
                ":play_pause: : Bouton play/pause\n"+
                ":leftwards_arrow_with_hook: : Répéter la liste de lecture " +
                ":arrows_counterclockwise: : Répéter la musique en cours\n" +
                ":wastebasket: : Supprimer le lecteur "+
                ":loud_sound: : (Dés)activer les basses",false);
        builder.addField("Status:", (player.isPaused() ? "En pause" : "En cours de lecture"), true);
        if(channel.getGuild().getAudioManager().isConnected()){
            int n = channel.getGuild().getAudioManager().getConnectedChannel().getMembers().size() - 1;
            builder.addField("Auditeurs:", "Il y à **"+n+"** auditeur(s)", true);
        }
        builder.addField("Liste de lecture:", "[ **"+musicManager.scheduler.getQueue().size()+
                "** musiques restantes]", true);
    }
}
