package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandClearPlaylist extends Command {

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
