package be.afhistos.satellitev2.server;

import java.io.Serializable;

public class VulcainPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private Object value;

    public VulcainPacket(PacketType type,String key, Object value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
