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

public class CommandVolume extends SlashCommand {

    public CommandVolume(){
        this.name ="volume";
        this.aliases = new String[]{"vol", "v"," metleswattgamin"};
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.INTEGER, "volume", "Le volume du bot <0-150>", true));
        this.options = data;
        this.help = "Définis le volume du bot dans le serveur actuel";
        this.guildOnly=  true;
        this.category=  new Category("Musique");
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        int vol = e.getOption("volume").getAsInt();
        if(vol > 200){
            e.reply(e.getClient().getError() + " Le volume doit être maximum à 200%!").queue();
            return;
        }
        if(!e.getMember().isOwner()) {
            if (vol < 0 && vol > 150) {
                e.reply(e.getClient().getWarning() + " Le volume doit être compris entre 0 et 150 !").queue();
                return;
            }
        }
        if(AudioUtils.getInstance().setVolume(e.getGuild(), vol)){
            e.reply("Le volume est maintenant à "+vol+"%").queue();
        }else{
            e.reply("Bizarre, quelque chose m'empêche de modifier le volume :thinking:").queue();
        }
    }
}
