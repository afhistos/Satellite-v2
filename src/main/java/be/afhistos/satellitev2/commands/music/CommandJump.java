package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandJump extends SlashCommand {

    public CommandJump(){
        this.name = "jump";
        this.category = new Category("Musique");
        this.help = "Permet de sauter à un instant précis du morceau joué par le bot.";
        this.aliases = new String[]{"goto"};
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "goto", "minutes:secondes", true));
        this.options = data;
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent e) {

        if(AudioUtils.getInstance().getPlayingTrack(e.getGuild()) == null){
            e.reply(e.getClient().getError() + " je ne joue aucun morceau sur ce serveur !").queue();
            return;
        }
        String[] args = e.getOption("goto").getAsString().split(":");
        long pos = TimeUnit.MINUTES.toMillis(Long.parseLong(args[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(args[1]));
        AudioUtils.getInstance().getPlayingTrack(e.getGuild()).setPosition(pos);
        e.reply("Le bot joue à présent **"+AudioUtils.getInstance().getPlayingTrack(e.getGuild()).getInfo().title+
                "** à partir de "+AudioUtils.getInstance().getMusicTimestamp(pos)).queue();


    }
}

