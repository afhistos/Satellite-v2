package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.sql.SQLUtils;

import java.util.ArrayList;

public class VulcainConnection {
    private String token;
    private ArrayList<String> serversId;
    private SQLUtils sqlInstance;

    public VulcainConnection(String token, SQLUtils sqlInstance) {
        this.token = token;
        this.sqlInstance = sqlInstance;
        serversId = new ArrayList<>();
    }
}
