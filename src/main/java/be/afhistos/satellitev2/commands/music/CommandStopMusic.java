package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandStopMusic extends CommandBase {

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
