package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.TimeUnit;

public class CommandPlay extends Command {

    public CommandPlay(){
        this.name = "play";
        this.help = "Permet de jouer de la musique";
        this.guildOnly = true;
        this.category = new Category("Musique");
        this.arguments = "<Lien/[Nombre de résultats] Recherche youtube>";
    }

    @Override
    protected void execute(CommandEvent e) {
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
        boolean insertFirst = false, forceJoin = false, clear = false;
        SearchType searchType = SearchType.YOUTUBE;
        String[] splittedArgs = e.getArgs().split("\\s+");
        String temp;
        StringBuilder query = new StringBuilder();
        for (String split : splittedArgs){
            if(split.startsWith("--")){
                temp = split.substring(2);
                insertFirst = temp.equalsIgnoreCase("i");
                forceJoin = temp.equalsIgnoreCase("fc");
                clear = temp.equalsIgnoreCase("c");
            }else if(split.startsWith("-")){
                temp = split.substring(1);
                insertFirst = insertFirst || temp.equalsIgnoreCase("insert");
                forceJoin = forceJoin || temp.equalsIgnoreCase("forcejoin");
                clear = clear || temp.equalsIgnoreCase("clear");
                if(searchType == SearchType.UNKNOWN){
                    searchType = SearchType.fromKey(temp);
                }
            }else{
                query.append(split);
            }
        }

        String timecode = "0";
        int i=1; //Default value
        String first;
        if(e.getArgs().startsWith("http://") || e.getArgs().startsWith("https://")){
            if(query.toString().contains("&t=")){
                timecode = query.toString().split("&t=")[1];
                timecode = timecode.replaceAll("[^0-9]", "");
            }
        }else{
            first = query.toString().split("\\s")[0];
            if(first.matches("[0-9]")){
                i = Integer.parseInt(first);
            }
            query.insert(0, searchType.getSearchPrefix());
        }
        if(forceJoin && BotUtils.isOwner(e.getMember())){
            if(e.getMember().getVoiceState().inAudioChannel()){
                e.getSelfMember().getGuild().getAudioManager().openAudioConnection(e.getMember().getVoiceState().getChannel());
            }
        }

        long pos = TimeUnit.SECONDS.toMillis(Long.parseLong(timecode));
        AudioUtils.getInstance().loadAndPlay(e.getTextChannel(), query.toString(), i, insertFirst, clear, pos);


    }

    public enum SearchType{
        YOUTUBE("yt"),
        YOUTUBE_MUSIC("ytm"),
        SOUNDCLOUD("sc"),
        UNKNOWN("unknown");

        private final String key;

        SearchType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public String getSearchPrefix(){
            return key+"search:";
        }

        public static SearchType fromKey(String key){
            SearchType[] listOf = values();
            int length = listOf.length;
            for (int i = 0; i < length; i++) {
                SearchType toValidate = listOf[i];
                if(toValidate.key.equalsIgnoreCase(key)){
                    return toValidate;
                }

            }
            return UNKNOWN;
        }
    }
}
