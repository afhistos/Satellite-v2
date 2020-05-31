package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandNowPlaying extends Command {

    public CommandNowPlaying(){
        this.name = "nowPlaying";
        this.aliases = new String[]{"np", "nowp","shazam"};
        this.category=  new Category("Musique");
        this.guildOnly=  true;
        this.help = "Permet de connaitre le morceaux actuellement joué par le bot.";
    }

    @Override
    protected void execute(CommandEvent e) {
        e.reply(AudioUtils.getInstance().getNowPlayingEmbed(e.getGuild(), e.getAuthor()).build());
    }
}
