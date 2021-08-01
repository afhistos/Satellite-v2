package be.afhistos.satellitev2.server;


import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import net.dv8tion.jda.api.entities.Guild;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class VulcainServer extends WebSocketServer implements Runnable {
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
        BotUtils.log(LogLevel.INFO, "Nouvelle connexion depuis: "+webSocket.getRemoteSocketAddress(), true, true);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        BotUtils.log(LogLevel.INFO, "fermeture de la connexion "+webSocket.getRemoteSocketAddress() + " depuis le "+(remote ? "client": "serveur") + ". Code: "+code, true, true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        BotUtils.log(LogLevel.INFO, "Message WebSocket ["+webSocket.getRemoteSocketAddress()+"]: "+message, true, true);

    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        BotUtils.log(LogLevel.INFO, "Message WebSocket (ByteBuff) ["+conn.getRemoteSocketAddress()+"]: "+message, true, true);
    }
    @Override
    public void onError(WebSocket webSocket, Exception e) {
        BotUtils.log(LogLevel.ERROR, "Erreur WebSocket ["+webSocket.getRemoteSocketAddress()+"]: "+e.getMessage(), true, true);

    }

    @Override
    public void onStart() {
        BotUtils.log(LogLevel.INFO, "[Inner]Serveur WebSocket démarré", true, true);
    }

}
