package be.afhistos.satellitev2;

import java.io.Serializable;

public class GuildInfo implements Serializable {

    public static final long serialVersionUID = 2310L;


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
