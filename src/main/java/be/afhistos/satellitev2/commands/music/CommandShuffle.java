package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandShuffle extends CommandBase {

    public CommandShuffle(){
        this.name = "shuffle";
        this.aliases = new String[]{"melanger", "shufflePlaylist", "shpl"};
        this.category = new Category("Musique");
        this.guildOnly = true;
        this.help = "Permet de mélanger la playlist que le bot est en train de jouer";
    }

    @Override
    protected void execute(CommandEvent e) {
        AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.shuffle();
        e.reactSuccess();
    }
}
