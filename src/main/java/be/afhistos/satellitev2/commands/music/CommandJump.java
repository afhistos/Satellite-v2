package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

import java.util.concurrent.TimeUnit;

public class CommandJump extends CommandBase {

    public CommandJump(){
        this.name = "jump";
        this.category = new Category("Musique");
        this.help = "Permet de sauter à un instant précis du morceau joué par le bot.";
        this.aliases = new String[]{"goto"};
        this.arguments = "<minutes:secondes>";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent e) {
        if(e.getArgs().isEmpty()){
            e.replyError("Impossible de retrouver la position souhaitée");
            return;
        }
        if(AudioUtils.getInstance().getPlayingTrack(e.getGuild()) == null){
            e.replyError("je ne joue aucun morceau sur ce serveur !");
            return;
        }
        String[] args = e.getArgs().split(":");
        long pos = TimeUnit.MINUTES.toMillis(Long.parseLong(args[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(args[1]));
        AudioUtils.getInstance().getPlayingTrack(e.getGuild()).setPosition(pos);
        e.reply("Le bot joue à présent **"+AudioUtils.getInstance().getPlayingTrack(e.getGuild()).getInfo().title+
                "** à partir de "+AudioUtils.getInstance().getMusicTimestamp(pos));


    }
}
