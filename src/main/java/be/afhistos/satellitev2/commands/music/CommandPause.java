package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandPause extends CommandBase {

    public CommandPause(){
        this.name = "pause";
        this.category = new Category("Musique");
        this.guildOnly = true;
        this.help = "Permet de mettre en pause ou de résumer la lecture du morceau actuel";
    }

    @Override
    protected void execute(CommandEvent e) {
        GuildMusicManager manager  = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild());
        manager.player.setPaused(!manager.player.isPaused());
        e.reactSuccess();

    }
}
