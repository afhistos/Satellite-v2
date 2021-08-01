package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandStopMusic extends Command {

    public CommandStopMusic(){
        this.name ="stopMusic";
        this.aliases = new String[]{"stop", "sm"};
        this.guildOnly = true;
        this.help = "Stoppe la musique jouée par le bot";
        this.category = new Category("Musique");

    }

    @Override
    protected void execute(CommandEvent e) {
        AudioUtils.getInstance().stopMusic(e.getGuild());
        e.reply("Musique stoppée.");
    }
}
