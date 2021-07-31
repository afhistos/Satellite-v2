package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.StartHandler;
import be.afhistos.satellitev2.commands.handler.CommandBase;
import be.afhistos.satellitev2.commands.handler.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CommandEval extends CommandBase {

    private ScriptEngine engine;

    public CommandEval(){
        this.name = "eval";
        this.aliases = new String[]{"evaluate", "test", "inner"};
        this.help = "Éxécute du code java dynamiquement.";
        this.guildOnly = false;
        this.ownerCommand = true;
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try{
            engine.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "Packages.be.afhistos.satellitev2,"+
                    "Packages.be.afhistos.satellitev2.server,"+
                    "Packages.net.dv8tion.jda.api," +
                    "Packages.net.dv8tion.jda.api.entities," +
                    "Packages.net.dv8tion.jda.api.entities.impl," +
                    "Packages.net.dv8tion.jda.api.managers," +
                    "Packages.net.dv8tion.jda.api.managers.impl," +
                    "Packages.net.dv8tion.jda.api.utils);");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        try{
            engine.put("event", event);
            engine.put("message", event.getMessage());
            engine.put("channel", event.getChannel());
            engine.put("args", event.getArgs());
            engine.put("api", event.getJDA());
            engine.put("start", StartHandler.class);
            engine.put("satellite", Satellite.class);
            engine.put("utils", new BotUtils());

            if(event.isFromType(ChannelType.TEXT)){
                engine.put("guild", event.getGuild());
                engine.put("member", event.getMember());
            }
            Object out = engine.eval(
                    "(function() {with (imports) {"+
                            event.getArgs()+"}})();");
            if(out == null){
                event.replyError("Une erreur est survenue lors de l'éxécution du code.");
            }else{
                event.reply(out.toString());
            }
        }catch (Exception e){
            event.replyError("Une erreur est survenue lors de l'éxécution du code:\n"+
                    "```Java\n"+ e.getMessage()+"\n```");
        }
    }
}
