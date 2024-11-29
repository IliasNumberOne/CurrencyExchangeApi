package com.iliasdev;

import com.iliasdev.util.ConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        List<Integer> ids = getAllCurrency();
        ids.forEach(System.out::println);

    }

    private static List<Integer> getAllCurrency() {
        String sql = "Select * from currencies";

        List<Integer> list = new ArrayList<Integer>();

        try(var connection = ConnectionManager.open();
            var preparedStatement = connection.prepareStatement(sql))
        {
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getObject("id", Integer.class));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
