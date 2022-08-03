package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandSkip extends SlashCommand {

    public CommandSkip(){
        this.name = "skip";
        this.help = "Permet de sauter 1 ou plusieurs morceaux joués par le bot";
        this.guildOnly = true;
        this.category = new Category("Musique");
        this.arguments = "[nombre de sauts]";
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.INTEGER, "skips", "Nombre de morceaux à sauter"));
        this.options = data;
    }
    @Override
    protected void execute(SlashCommandEvent e) {
        int skip;
        if(e.getOption("skips").getAsInt() == 0){
            skip = 1;
        }else{
            skip = e.getOption("skips").getAsInt();
        }
        if(skip > AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.getQueue().size()){
            e.reply(e.getClient().getWarning() + " La playlist n'est pas aussi grande! arrêt de la musique en cours...").queue();
            return;

        }
        for (int i = 1; i <= skip; i++) {
            AudioUtils.getInstance().skipSong(e.getGuild());
        }
        e.reply("Saut de "+skip+" morceau(x).").queue();
    }
}
