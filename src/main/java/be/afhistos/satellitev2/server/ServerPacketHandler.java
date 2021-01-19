package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.StartHandler;

public class ServerPacketHandler {

    private SatelliteServerThread s;

    public ServerPacketHandler(SatelliteServerThread s){
        this.s = s;
    };

    public void handlePacket(VulcainPacket v){
        StartHandler.getSatelliteServer().log("PACKET_IN : K:"+v.getKey()+"   V:"+v.getValue().toString());
        if(v.getKey().equals("Disconnect") && v.getValue().equals("Disconnect")){
            StartHandler.getSatelliteServer().disconnect(s);
        }

    }
}
