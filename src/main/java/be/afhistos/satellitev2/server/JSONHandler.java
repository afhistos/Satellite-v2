package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.Satellite;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class JSONHandler {

    public static String handle(JSONObject json){
        String action = json.getString("action");
        String guild = json.getString("guild");
        JSONObject result;
        switch (action){
            case "getStats":
                result = stats(guild);
                break;
            case "getGuildList":
                result = guildList();
                break;
            default:
                result = null;
        }

        return result != null ? result.toString() : null;

    }

    public static JSONObject isJsonValid(String json){
        try{
            return new JSONObject(json);
        }catch (JSONException ign){
            return null;
        }
    }

    private static JSONObject stats(String guildId){
        JSONObject toReturn = new JSONObject();
        Guild guild = Satellite.getBot().getGuildById(guildId);
        toReturn.put("action","sendStats");
        toReturn.put("guild", guildId);
        JSONObject roleArray = new JSONObject();
        roleArray.put("count", guild.getRoles().size());
        roleArray.put("higher", guild.getRoles().get(0).getName());
        toReturn.put("roles", roleArray);
        JSONObject memberArray = new JSONObject();
        memberArray.put("count", guild.getMemberCount());
        memberArray.put("owner", guild.getOwner().getUser().getAsTag());
        toReturn.put("members", memberArray);
        toReturn.put("name", guild.getName());
        toReturn.put("iconUrl", guild.getIconUrl());
        toReturn.put("bannerUrl", guild.getBannerUrl());
        JSONObject boostArray = new JSONObject();
        boostArray.put("tier", guild.getBoostTier());
        boostArray.put("count", guild.getBoostCount());
        boostArray.put("role", guild.getBoostRole() == null ? "null" : guild.getBoostRole().getName());
        boostArray.put("booster", new JSONArray(guild.getBoosters().stream().map(Member::getEffectiveName).collect(Collectors.toList())));
        toReturn.put("boost", boostArray);

        return toReturn;
    }

    public static JSONObject guildList(){
        JSONObject toReturn = new JSONObject();
        toReturn.put("action", "GuildList");
        JSONArray array = new JSONArray();
        JSONObject item;
        for (Guild g : Satellite.getBot().getGuilds()) {
            item = new JSONObject();
            item.put("name", g.getName()).put("id", g.getId()).put("owner", g.getOwner().getEffectiveName());
            array.put(item);
        }
        toReturn.put("data",array);
        return toReturn;
    }

}
