package be.afhistos.satellitev2.server;


import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import net.dv8tion.jda.api.entities.Guild;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class VulcainServer extends WebSocketServer {
    public int port;
    private JSONObject firstJson;

    public VulcainServer(int port) throws UnknownHostException {
        super(new InetSocketAddress("localhost",port));
        this.port = port;
        firstJson = new JSONObject();
        firstJson.put("action", "GuildList");
        JSONArray array = new JSONArray();
        JSONObject item;
        for (Guild g : Satellite.getBot().getGuilds()) {
            item = new JSONObject();
            item.put("name", g.getName()).put("id", g.getId()).put("owner", g.getOwner().getEffectiveName());
            array.put(item);
        }
        firstJson.put("data",array);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send(firstJson.toString());
        webSocket.sendPing();
        System.out.println("["+BotUtils.getTimestamp(System.currentTimeMillis(), false)+"] nouvelle connexion depuis: "+webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        System.out.println("closed " + webSocket.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        System.out.println("received message from "	+ webSocket.getRemoteSocketAddress() + ": " + message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }
    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.err.println("an error occurred on connection " + webSocket.getRemoteSocketAddress()  + ":" + e);

    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }

}
