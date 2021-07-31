package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandVolume extends CommandBase {

    public CommandVolume(){
        this.name ="volume";
        this.aliases = new String[]{"vol", "v"," metleswattgamin"};
        this.arguments= "<volume>";
        this.help = "Définis le volume du bot dans le serveur actuel";
        this.guildOnly=  true;
        this.category=  new Category("Musique");
    }

    @Override
    protected void execute(CommandEvent e) {
        if(e.getArgs() == null || e.getArgs() == ""){return;}
        int vol = Integer.parseInt(e.getArgs().split(" ")[0]);
        if(vol > 200){
            e.replyError("Le volume doit être maximum à 200%!");
            return;
        }
        if(!e.isOwner()) {
            if (vol < 0 && vol > 150) {
                e.replyWarning("Le volume doit être compris entre 0 et 150 !");
                return;
            }
        }
        if(AudioUtils.getInstance().setVolume(e.getGuild(), vol)){
            e.reply("Le volume est maintenant à "+vol+"%");
        }else{
            e.replyError("Bizarre, quelque chose m'empêche de modifier le volume :thinking:");
        }
    }
}
