package be.afhistos.satellitev2.consoleUtils;

import be.afhistos.satellitev2.consoleUtils.TextColor;

public enum LogLevel {
    INFO("Info"),
    WARNING("Warning"),
    ERROR("Error"),
    SYSTEM("System"),
    CONFIG("Config"),
    UNKNOWN("Unknown");

    private final String key;
    LogLevel(String key){this.key = key;}
    public String getKey(){return key;}

    public LogLevel fromKey(String key){
        LogLevel[] var1 = values();
        int var2 = var1.length;
        for (int i = 0; i < var2; i++) {
            LogLevel type = var1[i];
            if(type.key.equals(key)){
                return type;
            }
        }
        return UNKNOWN;
    }
    public String getColor(){
        return getColor(fromKey(key));
    }
    public String getColor(LogLevel logLevel){
        switch (logLevel){
            case UNKNOWN:
                return TextColor.BRIGHT_WHITE;
            case ERROR:
                return TextColor.RED;
            case CONFIG:
                return TextColor.BRIGHT_GREEN;
            case SYSTEM:
                return TextColor.BG_RED;
            case WARNING:
                return TextColor.BRIGHT_YELLOW;
            default:
                return TextColor.WHITE;
        }
    }
}
