package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;

public class CommandStopBot extends CommandBase {

    public CommandStopBot(){
        this.name= "stopbot";
        this.aliases = new String[]{"sb", "forcestop", "fs"};
        this.ownerCommand = true;
        this.guildOnly = false;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent e) {
        Satellite.setRunning(false);
    }
}
