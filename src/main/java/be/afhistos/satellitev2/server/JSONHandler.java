package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.Satellite;
import com.sun.security.auth.callback.TextCallbackHandler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
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
        Guild guild = Satellite.getBot().getGuildById(guildId);
        var wrapper = new Object(){int inviteCount = 0;boolean retrievedInvites = false;};
        guild.retrieveInvites().queue(invites -> {
            wrapper.inviteCount = invites.size();
            wrapper.retrievedInvites = true;
        });
        JSONObject toReturn = new JSONObject();
        toReturn.put("action","sendStats");
        JSONObject guildArray = new JSONObject();
        guildArray.put("id", guild.getId());
        guildArray.put("BoostCount",guild.getBoostCount());
        guildArray.put("name", guild.getName());
        guildArray.put("iconUrl", guild.getIconUrl());
        //guildArray.put("InviteCount",);
        guildArray.put("Creation", guild.getTimeCreated().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));

        JSONObject roleArray = new JSONObject();
        roleArray.put("Count", guild.getRoles().size());
        roleArray.put("Higher", guild.getRoles().get(0).getName());
        roleArray.put("MostPopular", getMostPopularRole(guild));
        roleArray.put("Default", guild.getPublicRole().getName());
        roleArray.put("Boosters", guild.getBoostRole() == null ? "Aucun" : guild.getBoostRole().getName());

        JSONObject memberArray = new JSONObject();
        memberArray.put("Count", guild.getMemberCount());
        memberArray.put("ConnectedCount", getConnectedMemberCount(guild));
        memberArray.put("AdminCount", getAdminMemberCount(guild));
        memberArray.put("BoostersCount", guild.getBoostCount());
        memberArray.put("Owner", guild.getOwner().getUser().getAsTag());
        memberArray.put("Last", getLastMember(guild));
        memberArray.put("GanymedeJoin", guild.getSelfMember().getTimeJoined().toLocalDate().format(DateTimeFormatter.ofPattern("le dd/MM/yyyy à hh:mm:ss")));

        JSONObject channelArray = new JSONObject();
        channelArray.put("Count", guild.getChannels().size());
        channelArray.put("CatCount", guild.getCategories().size());
        channelArray.put("TextCount",guild.getTextChannels().size());
        channelArray.put("Count",guild.getV);
        channelArray.put("Count",);
        channelArray.put("Count",);
        channelArray.put("booster", new JSONArray(guild.getBoosters().stream().map(Member::getEffectiveName).collect(Collectors.toList())));

        toReturn.put("members", memberArray);
        toReturn.put("guild", guildArray);
        toReturn.put("roles", roleArray);
        toReturn.put("channels", channelArray);
        return toReturn;
    }

    private static int getAdminMemberCount(Guild guild) {
        int count = 0;
        for(Member member: guild.getMembers()){
            if(member.hasPermission(Permission.ADMINISTRATOR)){
                count++;
            }
        }
        return count;
    }

    private static int getConnectedMemberCount(Guild guild) {
        int count = 0;
        for(Member member : guild.getMembers()){
            if(!member.getOnlineStatus().equals(OnlineStatus.OFFLINE)){
                count++;
            }
        }
        return count;
    }

    private static String getLastMember(Guild guild) {
        HashMap<Date, Member> dates = new HashMap<>();
        long now = System.currentTimeMillis();
        for(Member member : guild.getMembers()){
            dates.put(Date.from(Instant.from(member.getTimeJoined())), member);
        }
        Date newest = Collections.min(dates.keySet(), (d1, d2) -> {
            long diff1 = Math.abs(d1.getTime() - now);
            long diff2 = Math.abs(d2.getTime() - now);
            return Long.compare(diff1, diff2);
        });
        return dates.get(newest).getEffectiveName()+" (le "+ new SimpleDateFormat("EEEE dd/MM/yyyy à HH:mm:ss").format(newest) +")";
    }



    private static String getMostPopularRole(Guild guild) {
        //Since we can create roles with the same name, we need to store the roles by their id
        HashMap<String, Integer> roleHashMap = new HashMap<>();
        for(Member member: guild.getMembers()){
            for(Role memberRole: member.getRoles()){
                //Increment the Integer value of roleHashMap, and add role key with 1 as value if role isn't in HashMap
                roleHashMap.merge(memberRole.getId(), 1, Integer::sum);
            }
        }
        int count = 0;
        AtomicReference<String> mostPopular = null;
        roleHashMap.forEach((roleId, userCount) ->{
            if(userCount > count){
                mostPopular.set(roleId);
            }
        });
        return guild.getRoleById(mostPopular.get()).getName();
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
