package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandStopMusic extends SlashCommand {

    public CommandStopMusic(){
        this.name ="stopmusic";
        this.aliases = new String[]{"stop", "sm"};
        this.guildOnly = true;
        this.help = "Stoppe la musique jouée par le bot";
        this.category = new Category("Musique");

    }

    @Override
    protected void execute(SlashCommandEvent e) {
        AudioUtils.getInstance().stopMusic(e.getGuild());
        e.reply("Musique stoppée.").queue();
    }
}
