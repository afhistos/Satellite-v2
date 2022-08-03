package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.audio.GuildMusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandPause extends SlashCommand {

    public CommandPause(){
        this.name = "pause";
        this.category = new Category("Musique");
        this.guildOnly = true;
        this.help = "Permet de mettre en pause ou de résumer la lecture du morceau actuel";
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        GuildMusicManager manager  = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild());
        boolean status = !manager.player.isPaused();
        manager.player.setPaused(status);
        e.reply(status ? "Mise en pause!" : "Reprise de la lecture!").queue();

    }
}
