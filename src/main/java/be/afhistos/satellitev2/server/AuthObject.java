package be.afhistos.satellitev2.server;

import java.util.ArrayList;
import java.util.List;

public class AuthObject {
    private int id;
    private String userId;
    private boolean isAdmin;
    private ArrayList<String> serversId;

    public AuthObject(int id, String userId, boolean isAdmin, ArrayList<String> serversId) {
        this.id = id;
        this.userId = userId;
        this.isAdmin = isAdmin;
        this.serversId = serversId;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public ArrayList<String> getServersId() {
        return serversId;
    }

    public void addServerIds(String... id){
        serversId.addAll(List.of(id));
    }

    public void removeServerIds(String... id){
        serversId.removeAll(List.of(id));
    }

    public class Builder{

    }
}
