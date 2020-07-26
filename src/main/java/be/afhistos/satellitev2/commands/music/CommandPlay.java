package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.managers.AudioManager;

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
        AudioManager audio = e.getGuild().getAudioManager();
        if(!audio.isConnected()){
            if(e.getMember().getVoiceState().inVoiceChannel()){
                AudioUtils.getInstance().connectToVoiceChannel(audio, e.getMember().getVoiceState().getChannel(), true);
            }else{
                AudioUtils.getInstance().connectToFirstVoiceChannel(audio);
            }
        }
        String query;
        int i=20; //Default value
        String first;
        if(e.getArgs().startsWith("http://") || e.getArgs().startsWith("https://")){
            query = e.getArgs();
        }else{
            query = e.getArgs();
            first = query.split("\\s")[0];
            if(first.matches("[0-9]")){
                i = Integer.parseInt(first);
                query = query.replace(first, "");
            }
            query = "ytsearch:"+ query;
        }
        AudioUtils.getInstance().loadAndPlay(e.getTextChannel(),  query, i);
    }
}
