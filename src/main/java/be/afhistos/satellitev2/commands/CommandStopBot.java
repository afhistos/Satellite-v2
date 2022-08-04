package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.Satellite;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class CommandStopBot extends SlashCommand {

    public CommandStopBot(){
        this.name= "stopbot";
        this.aliases = new String[]{"sb", "forcestop", "fs"};
        this.ownerCommand = true;
        this.guildOnly = false;
        this.hidden = true;
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        e.reply("Arrêt...").queue();
        Satellite.setRunning(false);
    }
}
