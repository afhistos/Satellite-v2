package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHandler {

    public static void handle(String json){
        if(!isJsonValid(json)){
            BotUtils.log(LogLevel.WARNING, "Impossible de traiter le json donné: "+json, true, false);
            return;
        }
        JSONObject obj = new JSONObject(json);

    }

    private static boolean isJsonValid(String json){
        try{
            new JSONObject(json);
        }catch (JSONException ign){
            return false;
        }
        return true;
    }
}
