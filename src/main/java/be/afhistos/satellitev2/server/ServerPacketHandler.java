package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.StartHandler;

public class ServerPacketHandler {
    private ServerCommunicationThread s;

    public ServerPacketHandler(ServerCommunicationThread s) {
        this.s = s;

    }

    public void handlePacket(VulcainPacket v) {
        StartHandler.getServerThread().log("PACKET_IN : K:" + v.getKey() + "   V:" + v.getValue().toString());
        if (v.getKey().equals("Disconnect") && v.getValue().equals("Disconnect")) {
            StartHandler.getServerThread().logout(s);
        }
    }
}
