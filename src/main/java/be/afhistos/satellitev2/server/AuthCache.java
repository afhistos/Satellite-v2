package be.afhistos.satellitev2.server;

import be.afhistos.satellitev2.sql.QueryResult;
import be.afhistos.satellitev2.sql.SQLUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthCache {
    private HashMap<String, AuthObject> auths;
    private SQLUtils utils;

    public AuthCache(SQLUtils utils){
        auths = new HashMap<>();
        this.utils = utils;
    }

    public void loadFromToken(String token){
        QueryResult qr = utils.getData("SELECT * FROM auth WHERE token = `?`", token);

    }



}
