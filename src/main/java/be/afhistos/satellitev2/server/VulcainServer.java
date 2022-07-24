package be.afhistos.satellitev2.server;


import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import org.apache.http.pool.PoolEntry;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class VulcainServer extends WebSocketServer implements Runnable {
    private InetSocketAddress address;
    private ArrayList<VulcainConnection> connectionPool;

    public VulcainServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.address = super.getAddress();
        connectionPool = new ArrayList<VulcainConnection>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        //Récupérer le token unique depuis l'app
        //connectionPool.add(new VulcainConnection());
        BotUtils.log(LogLevel.INFO, "Nouvelle connexion depuis: "+webSocket.getRemoteSocketAddress(), true, true);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        BotUtils.log(LogLevel.INFO, "fermeture de la connexion "+webSocket.getRemoteSocketAddress() + " depuis le "+(remote ? "client": "serveur") + ". Code: "+code, true, true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        BotUtils.log(LogLevel.INFO, "Message WebSocket ["+webSocket.getRemoteSocketAddress()+"]: "+message, true, true);
        JSONObject json = JSONHandler.isJsonValid(message);
        if(json != null){
            String result = JSONHandler.handle(json);
            if(result != null){
                webSocket.send(result);
            }
        }

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

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }
}
