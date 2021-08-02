package be.afhistos.satellitev2.consoleUtils;

public interface ConsoleListener {

    default void onConsoleMessage(String message){}
    default void onConsoleCommand(String command){}
}
