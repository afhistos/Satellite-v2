package be.afhistos.satellitev2.commands.handler;

import be.afhistos.satellitev2.commands.CommandConfinement;
import be.afhistos.satellitev2.commands.CommandEval;
import be.afhistos.satellitev2.commands.CommandMonitoring;
import be.afhistos.satellitev2.commands.CommandStopBot;
import be.afhistos.satellitev2.listeners.CommandWatcher;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler implements EventListener {
    private String prefix;
    private String successEmote;
    private String warningEmote;
    private String errorEmote;
    private String ownerId;
    private ArrayList<String> coOwnerIds = new ArrayList<>();
    private ArrayList<CommandBase> commands = new ArrayList<>();

    public CommandHandler(){

    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuccess() {
        return successEmote;
    }

    public void setSuccess(String successEmote) {
        this.successEmote = successEmote;
    }

    public String getWarning() {
        return warningEmote;
    }

    public void setWarning(String warningEmote) {
        this.warningEmote = warningEmote;
    }

    public String getError() {
        return errorEmote;
    }

    public void setError(String errorEmote) {
        this.errorEmote = errorEmote;
    }

    public void setEmojis(String success, String warning, String error){
        this.successEmote = success;
        this.warningEmote = warning;
        this.errorEmote = error;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public CommandHandler setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public ArrayList<String> getCoOwnerIds() {
        return coOwnerIds;
    }

    public void addCoOwnerIds(String... ids){
        for (String id: ids){
            if(!coOwnerIds.contains(id)){
                coOwnerIds.add(id);
            }
        }
    }

    public void removeCoOwnerIds(String... ids){
        for (String id: ids){
            coOwnerIds.remove(id);
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {

    }

    public void addCommands(CommandBase... newCommands) {
        commands.addAll(Arrays.asList(newCommands));
    }

    public void setListener(CommandListener listener) {
    }
}
