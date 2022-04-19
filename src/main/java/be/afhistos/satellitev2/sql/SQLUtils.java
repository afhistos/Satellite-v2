package be.afhistos.satellitev2.sql;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class SQLUtils {
    private static String url, log, pwd;
    private static Connection c;
    private static final Properties props = new Properties();

    private boolean autoClose;



    public SQLUtils(boolean autoClose){
        this.autoClose = autoClose;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("props.properties"));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
        url = props.getProperty("url");
        log = props.getProperty("login");
        pwd = props.getProperty("pwd");
        System.out.println("\n------------------------------------------\n"+
                "Config chargée"+
                "\n------------------------------------------\n");
    }

    public SQLUtils getInstance(){
        if(c == null){
            try{
                c = DriverManager.getConnection(url, log,pwd);
                System.out.println("\n------------------------------------------\n"+
                        "Bases de données connectée"+
                        "\n------------------------------------------\n");
            } catch (SQLException e) {
                System.out.println("Peux PAS ME CONNECTER\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                e.printStackTrace();
                return null;
            }
        }
        return this;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public void execute(String query){
        getInstance();
        Statement s = null;
        try {
            s = c.createStatement();
            if(s == null){
                throw new IllegalArgumentException("s is null silly");
            }
            s.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void execute(String query, Object... args){
        getInstance();
        PreparedStatement s = null;
        try{
            s= c.prepareStatement(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try{
            for (int i = 1; i <= args.length; i++) {
                s.setString(i, args[i-1].toString());
            }
            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public QueryResult getData(String query){
        getInstance();
        Statement s = null;
        ResultSet r = null;
        try {
            s = c.createStatement();
            if(s == null){
                throw new IllegalArgumentException("s is null silly");
            }
            r = s.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new QueryResult(s,r, this.autoClose);
    }

    //Ne pas s'en servir maintenant
    public QueryResult getData(String query, Object... args){
        getInstance();
        PreparedStatement s =null;
        ResultSet r = null;
        try{
            s = c.prepareStatement(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try{
            for (int i = 0; i < args.length; i++) {
                s.setString(i+1, args[i].toString());

            }
            r = s.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new QueryResult(s, r, this.autoClose);

    }
}
