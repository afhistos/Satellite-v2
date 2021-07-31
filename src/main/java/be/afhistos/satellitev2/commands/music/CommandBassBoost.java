package be.afhistos.satellitev2.commands.music;

import be.afhistos.satellitev2.audio.AudioUtils;
import be.afhistos.satellitev2.commands.handler.Category;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandBassBoost extends CommandBase {
    public CommandBassBoost(){
        this.name = "bassboost";
        this.aliases = new String[]{"bb", "bass", "boostbass"};
        this.guildOnly=  true;
        this.help = "Améliore les basses fréquences des morceaux joués par le bot.Suite à des problèmes de stabilité, seuls"+
        "les propriétaires peuvent utiliser cette commande";
        this.ownerCommand  = true;
        this.category = new Category("Musique");
    }

    @Override
    protected void execute(CommandEvent e) {
        AudioUtils.getInstance().invertBass(e.getGuild());
        boolean enabled = AudioUtils.getInstance().getGuildAudioPlayer(e.getGuild()).isBassActive();
        e.reply("Les basses fréquences sont maintenant "+ (enabled ? "activées" : "désactivées!"));
    }
}
