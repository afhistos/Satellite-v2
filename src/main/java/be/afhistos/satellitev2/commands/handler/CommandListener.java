package be.afhistos.satellitev2.commands.handler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

public interface CommandListener {

    default void onCommand(CommandEvent event, CommandBase command){}
    default void onCompletedCommand(CommandEvent event, CommandBase command) {}
    default void onTerminatedCommand(CommandEvent event, CommandBase command) {}
    default void onNonCommandMessage(MessageReceivedEvent event) {}
    default void onCommandException(CommandEvent event, CommandBase command, Throwable throwable) {
        // Default rethrow as a runtime exception.
        throw throwable instanceof RuntimeException? (RuntimeException)throwable : new RuntimeException(throwable);
    }
}
