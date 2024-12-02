package com.iliasdev.dao;

import com.iliasdev.model.Currency;
import com.iliasdev.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<Integer, Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    @Override
    public Currency create(Currency currency) {
        final String INSERT_SQL = """
                INSERT INTO currencies(code, full_name, sign) VALUES (?, ?, ?)
                """;

        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()) {
                currency.setId(keys.getInt(1));
            }

            return currency;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency findById(Integer id) {
        final String FIND_BY_ID_SQL = """
                SELECT * FROM currencies WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL))
        {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();

            Currency currency = null;
            if(resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            return currency;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Currency> findAll() {
        final String FIND_ALL_SQL= """
                SELECT * FROM currencies
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL))
        {
            var resultSet = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while(resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Currency currency) {
        final String UPDATE_SQL = """
                UPDATE currencies
                SET code = ?, full_name = ?, sign = ?
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
        var statement = connection.prepareStatement(UPDATE_SQL))
        {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setInt(4, currency.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Currency currency) {
        final String DELETE_SQL = """
                DELETE FROM currencies WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.open();
        var statement = connection.prepareStatement(DELETE_SQL))
        {
            statement.setInt(1, currency.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency findByCode(String code) {
        final String FIND_BY_CODE_SQL = """
                SELECT * FROM currencies WHERE code = ?
                """;
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL))
        {
            statement.setString(1, code);
            var resultSet = statement.executeQuery();
            Currency currency = null;
            if(resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
