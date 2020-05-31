package be.afhistos.satellitev2.database;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.StartHandler;
import be.afhistos.satellitev2.consoleUtils.LogLevel;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javafx.util.Pair;

import java.sql.*;
import java.util.Properties;

public class SQLUtils {
    private static String url, log, pwd;
    private Connection c;
    public SQLUtils(Properties props){
        url = props.getProperty("sql.url");
        log = props.getProperty("sql.login");
        pwd = props.getProperty("sql.password");
    }

    public SQLUtils getInstance(){
      if(c == null){
          try{
              Class.forName("com.mysql.cj.jdbc.Driver");
              c= DriverManager.getConnection(url, log,pwd);
              BotUtils.log(LogLevel.INFO, "Reconnexion automatique à la base de données", true, false);
          } catch (ClassNotFoundException e) {
              BotUtils.log(LogLevel.SYSTEM, "Impossible de trouver le driver MySQL", true, true);
              return null;
          } catch (SQLException throwables) {
              BotUtils.log(LogLevel.ERROR, "Impossible de se connecter à la base de données!", true, true);
              return null;
          }catch (Exception ign){
                return null;
          }
      }
      return this;
    }
    private Connection reconnect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, log, pwd);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public QueryResult execute(String query){
        Statement s = null;
        ResultSet r = null;
        if(c == null){
            c = reconnect();
        }
        try {
            s = c.createStatement();
            if (s == null) {
                return new QueryResult(null, null);
            }
            r = s.executeQuery(query);
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return new QueryResult(s,r);
    }
    public boolean executeData(String query){
        if(c == null){
            c = reconnect();
        }
        try{
          Statement s = c.createStatement();
          s.execute(query);
          return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
