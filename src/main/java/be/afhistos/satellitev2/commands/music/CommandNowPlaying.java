package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandNowPlaying extends CommandBase {

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
