package be.afhistos.satellitev2.server;

import java.io.Serializable;

public class GuildInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name, id, owner;

    public GuildInfo(String name, String id, String owner) {
        this.name = name;
        this.id = id;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }
}
