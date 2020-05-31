package be.afhistos.satellitev2.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryResult {
    private Statement s;
    private ResultSet r;

    public QueryResult(Statement s, ResultSet r){
        this.s = s;
        this.r = r;
    }

    public Statement getStatement() {
        return s;
    }

    public ResultSet getResult() {
        return r;
    }
    public void close(){
        try{
            r.close();
            s.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
