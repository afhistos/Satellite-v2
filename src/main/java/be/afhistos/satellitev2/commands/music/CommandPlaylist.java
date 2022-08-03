package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class CommandPlaylist extends SlashCommand {

    private final Paginator.Builder pBuilder;
    public CommandPlaylist(EventWaiter waiter){
        this.name = "playlist";
        this.aliases = new String[]{"pl", "playl", "plist","listSongs"};
        this.category = new Category("Musique");
        this.guildOnly = true;
        this.help = "Affiche les 10 prochains morceaux de la playlist.";
        pBuilder = new Paginator.Builder().setColumns(1)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .setFinalAction(message -> {
                    message.clearReactions().queue();
                })
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(waiter);

    }

    @Override
    protected void execute(SlashCommandEvent e) {
        pBuilder.clearItems();
        GuildMusicManager musicManager = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild());
        AudioTrack current = musicManager.player.getPlayingTrack();
        LinkedList<AudioTrack> queue = musicManager.scheduler.getQueue();
        long duration = 0;
        duration += current.getDuration() - current.getPosition();
        pBuilder.addItems(":arrow_forward: **"+current.getInfo().title+"** par "+current.getInfo().author+" ("
                +AudioUtils.getInstance().getMusicTimestamp(current.getPosition())+"/"
                +AudioUtils.getInstance().getMusicTimestamp(current.getDuration())+")");
        queue.stream().map(a ->"**"+a.getInfo().title+"** par "+a.getInfo().author+" ("
                        +AudioUtils.getInstance().getMusicTimestamp(a.getDuration())+")").forEach(pBuilder::addItems);
        for(AudioTrack track : queue){
            duration += track.getDuration();
        }
        Paginator p = pBuilder.setColor(BotUtils.getDefaultColor()).setText(e.getClient().getSuccess()+" Playlist de "+
                e.getGuild().getSelfMember().getEffectiveName()+" dans le serveur **"+e.getGuild().getName()+
                "**:\nTaille de la playlist: "+ (queue.size()+1)+
                "\n  Durée de la playlist: "+ BotUtils.getTimestamp(duration, false))
                .setUsers(e.getUser()).build();
        p.paginate(e.getChannel(), 1);

    }
}
