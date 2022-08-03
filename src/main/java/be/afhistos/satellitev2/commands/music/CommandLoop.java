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

public class CommandLoop extends SlashCommand {

    public CommandLoop(){
        this.name = "loop";
        this.aliases = new String[]{"repeat"};
        this.guildOnly = true;
        this.category = new Category("Musique");
        this.help = "Permet d'activer/désactiver la répétition d'un morceau ou de toute la playlist";
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.BOOLEAN, "repeatonce", "Vrai si le lecteur doit répeter le morceau en cours, Faux pour répeter la playlist"));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        if(e.getOption("repeatonce").getAsBoolean()){
            boolean a = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.invertRepeatingOnce();
            e.reply(e.getClient().getSuccess() + " Le morceau actuellement joué "+(a ? "sera" : "ne sera plus")+" répeté en boucle").queue();
        }else{
            boolean a = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.invertRepeatingPlaylist();
            e.reply(e.getClient().getSuccess() + " La playlist "+(a ? "sera" : "ne sera plus")+" répetée en boucle").queue();
        }
    }
}
