package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandPlay extends SlashCommand {

    public CommandPlay(){
        this.name = "play";
        this.help = "Permet de jouer de la musique";
        this.guildOnly = true;
        this.category = new Category("Musique");
        this.arguments = "<Lien/[Nombre de résultats] Recherche youtube>";
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "query", "Lien/Recherche youtube", true));
        data.add(new OptionData(OptionType.BOOLEAN, "insert", "Bypass la file d'attente", false));
        data.add(new OptionData(OptionType.INTEGER, "count", "Nombre de musiques à rechercher", false));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        //Connect to audio
        AudioManager audio = e.getGuild().getAudioManager();
        if(!audio.isConnected()){
            if(e.getMember().getVoiceState().inAudioChannel()){
                AudioUtils.getInstance().connectToVoiceChannel(audio, e.getMember().getVoiceState().getChannel(), true);
            }else{
                AudioUtils.getInstance().connectToFirstVoiceChannel(audio);
            }
        }

        AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild());//Load GuildAudioPlayer before playing

        //Handle args
        boolean insertFirst = e.getOption("insert").getAsBoolean();

        String timecode = "0";
        int i=1; //Default value
        String first;
        String query = e.getOption("query").getAsString();
        if(query.startsWith("http://") || query.startsWith("https://")){
            if(query.toString().contains("&t=")){
                timecode = query.toString().split("&t=")[1];
                timecode = timecode.replaceAll("[^0-9]", "");
            }
        }else{
            first = query.split("\\s")[0];
            if(first.matches("[0-9]")){
                i = Integer.parseInt(first);
            }
            query = "yt" + query;
        }

        long pos = TimeUnit.SECONDS.toMillis(Long.parseLong(timecode));
        AudioUtils.getInstance().loadAndPlay(e.getTextChannel(), query.toString(), i, insertFirst, false, pos);


    }

}
