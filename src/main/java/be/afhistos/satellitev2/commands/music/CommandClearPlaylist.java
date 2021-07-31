package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandClearPlaylist extends CommandBase {

    public CommandClearPlaylist(){
        this.name = "clearpl";
        this.aliases = new String[]{"clear", "cpl", "deletepl", "delpl"};
        this.help = "Vide la liste de lecture";
        this.guildOnly = true;
        this.category = new Category("Musique");
    }

    @Override
    protected void execute(CommandEvent e) {
        AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.clearQueue();
        e.reply("La playlist à bien été vidée!");
    }
}
