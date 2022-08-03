package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandNowPlaying extends SlashCommand {

    public CommandNowPlaying(){
        this.name = "nowplaying";
        this.aliases = new String[]{"np", "nowp","shazam"};
        this.category=  new Category("Musique");
        this.guildOnly=  true;
        this.help = "Permet de connaitre le morceaux actuellement joué par le bot.";
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        e.replyEmbeds(AudioUtils.getInstance().getNowPlayingEmbed(e.getGuild(), e.getUser()).build()).queue();
    }
}
