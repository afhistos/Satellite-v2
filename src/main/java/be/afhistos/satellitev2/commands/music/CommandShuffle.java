package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandShuffle extends SlashCommand {

    public CommandShuffle(){
        this.name = "shuffle";
        this.aliases = new String[]{"melanger", "shufflePlaylist", "shpl"};
        this.category = new Category("Musique");
        this.guildOnly = true;
        this.help = "Permet de mélanger la playlist que le bot est en train de jouer";
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.shuffle();
        e.reply("La playlist à été mélangée avec succès!").queue();
    }
}
