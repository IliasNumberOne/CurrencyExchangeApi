package com.iliasdev.dao;

import com.iliasdev.model.ExchangeRates;
import com.iliasdev.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDao implements Dao<Integer, ExchangeRates>{
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Override
    public ExchangeRates create(ExchangeRates exchangeRates) {
        final String CREATE_SQL = """
                INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) 
                VALUES (?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, exchangeRates.getBaseCurrency().getId());
            statement.setInt(2, exchangeRates.getTargetCurrency().getId());
            statement.setDouble(3, exchangeRates.getRate());
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if(keys.next()) {
                exchangeRates.setId(keys.getInt(1));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRates findById(Integer id) {
        final String FIND_BY_ID = """
                SELECT * FROM exchange_rates WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID)) 
        {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            return buildExchangeRates(resultSet);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRates> findAll() {
        final String FIND_ALL_SQL = """
                SELECT * FROM exchange_rates
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL))
        {
            var resultSet = statement.executeQuery();

            List<ExchangeRates> exchangeRatesList = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRatesList.add(buildExchangeRates(resultSet));
            }

            return exchangeRatesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void update(ExchangeRates exchangeRates) {
        final String UPDATE_SQL = """
                UPDATE exchange_rates
                SET base_currency_id = ?, target_currency_id = ?, rate = ?
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_SQL))
        {
            statement.setInt(1, exchangeRates.getBaseCurrency().getId());
            statement.setInt(2, exchangeRates.getTargetCurrency().getId());
            statement.setDouble(3, exchangeRates.getRate());
            statement.setInt(4, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ExchangeRates exchangeRates) {
        final String DELETE_SQL = """
                DELETE FROM exchange_rates
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
        var statement = connection.prepareStatement(DELETE_SQL))
        {
            statement.setInt(1, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeRates findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        final String FIND_BY_CODES_SQL = """
                select er.id as id, 
                       baseCr.id as base_currency_id, 
                       targetCr.id as target_currency_id, 
                       er.rate as rate
                from exchange_rates er
                         join currencies baseCr on baseCr.id = er.base_currency_id
                         join currencies targetCr on targetCr.id = er.target_currency_id
                where baseCr.code = ?
                  and targetCr.code = ?;
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_CODES_SQL))
        {
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return buildExchangeRates(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(
                resultSet.getInt("id"),
                currencyDao.findById(resultSet.getInt("base_currency_id")),
                currencyDao.findById(resultSet.getInt("target_currency_id")),
                resultSet.getDouble("rate")
        );
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }
}
