package org.example.os;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login {

    public int login(String name, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        Statement sql = connection.createStatement();
        String SQL = "select * from user where username = \'" + name + "\' and password =\'" + password + "\' ";
        ResultSet res = sql.executeQuery(SQL);
        boolean result = res.next();
        if (result) {
            User user = new User();
            user.setId(res.getInt("id"));
            user.setGroup(res.getInt("group"));
            DBConnection.closeConnection(connection);
            return user.getId();

        } else {
            DBConnection.closeConnection(connection);
            return 0;
        }
    }
}
