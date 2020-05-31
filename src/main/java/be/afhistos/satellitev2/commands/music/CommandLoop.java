package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandLoop extends Command {

    public CommandLoop(){
        this.name = "loop";
        this.aliases = new String[]{"repeat"};
        this.guildOnly = true;
        this.category = new Category("Musique");
        this.help = "Permet d'activer/désactiver la répétition d'un morceau ou de toute la playlist";
        this.arguments = "[\"once\"]";
    }

    @Override
    protected void execute(CommandEvent e) {
        if(!e.getArgs().isEmpty() && e.getArgs().equalsIgnoreCase("once")){
            boolean a = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.invertRepeatingOnce();
            e.replySuccess("Le morceau actuellement joué "+(a ? "sera" : "ne sera plus")+" répeté en boucle");
        }else{
            boolean a = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).scheduler.invertRepeatingPlaylist();
            e.replySuccess("La playlist "+(a ? "sera" : "ne sera plus")+" répetée en boucle");
        }
    }
}
