package be.afhistos.satellitev2.server;

import java.io.Serializable;

enum PacketType implements Serializable {
    PACKET_CLIENT(0),

    PACKET_SERVER(1);

    private static final long serialVersionUID = 1L;
    private final int key;

    PacketType(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
