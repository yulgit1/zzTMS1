package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class TmsConnection {
    String host;
    String user;
    String pw;
    Connection conn;

    public TmsConnection(String host,
                         String user,
                         String pw) {
        this.host = host;
        this.user = user;
        this.pw = pw;
    }

    public Connection getConnection() {
        try {
            //https://stackoverflow.com/questions/2451892/how-do-i-connect-to-a-sql-server-2008-database-using-jdbc
            //http://jtds.sourceforge.net/faq.html
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(host, user, pw);
            System.out.println("connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

