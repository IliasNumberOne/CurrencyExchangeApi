package com.iliasdev;

import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.service.ExchangeCurrencyService;
import com.iliasdev.util.ConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        CurrencyDao currencyDao = CurrencyDao.getInstance();
        ExchangeCurrencyService service = ExchangeCurrencyService.getInstance();

        service.exchangeCurrency(currencyDao.findByCode("EUR").get(), currencyDao.findByCode("RUB").get(), 4.33);

    }

    private static List<Integer> getAllCurrency() {
        String sql = "Select * from currencies";

        List<Integer> list = new ArrayList<Integer>();

        try(var connection = ConnectionManager.getConnection();
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
