package com.iliasdev.dao;

import com.iliasdev.exception.DataBaseOperationException;
import com.iliasdev.exception.EntityExistException;
import com.iliasdev.model.CurrencyModel;
import com.iliasdev.model.ExchangeRates;
import com.iliasdev.util.ConnectionManager;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, CurrencyModel> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    @Override
    public CurrencyModel create(CurrencyModel currencyModel) {
        final String INSERT_SQL = """
                INSERT INTO currencies(code, full_name, sign) VALUES (?, ?, ?)
                """;

        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, currencyModel.getCode());
            statement.setString(2, currencyModel.getFullName());
            statement.setString(3, currencyModel.getSign());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()) {
                currencyModel.setId(keys.getInt(1));
            }

            return currencyModel;

        } catch (SQLException e) {
            if(e instanceof PSQLException) {
                PSQLException exception = (PSQLException) e;
                if(exception.getSQLState().equals("23505")) {
                    throw new EntityExistException("Currency with code " + currencyModel.getCode() + " already exists");
                }
            }
            throw new DataBaseOperationException("Failed to save currency with code: " + currencyModel.getCode() + " to the database");
        }
    }

    @Override
    public Optional<CurrencyModel> findById(Integer id) {
        final String FIND_BY_ID_SQL = """
                SELECT * FROM currencies WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL))
        {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to find currency with id: " + id + " from the database");
        }
    }


    @Override
    public List<CurrencyModel> findAll() {
        final String FIND_ALL_SQL= """
                SELECT * FROM currencies
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL))
        {
            var resultSet = statement.executeQuery();
            List<CurrencyModel> currencies = new ArrayList<>();
            while(resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to find currencies from the database");
        }
    }

    @Override
    public ExchangeRates update(CurrencyModel currencyModel) {
        final String UPDATE_SQL = """
                UPDATE currencies
                SET code = ?, full_name = ?, sign = ?
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL))
        {
            statement.setString(1, currencyModel.getCode());
            statement.setString(2, currencyModel.getFullName());
            statement.setString(3, currencyModel.getSign());
            statement.setInt(4, currencyModel.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to update currency with code: " + currencyModel.getCode() + " to the database");
        }
        return null;
    }

    @Override
    public void delete(CurrencyModel currencyModel) {
        final String DELETE_SQL = """
                DELETE FROM currencies WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL))
        {
            statement.setInt(1, currencyModel.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to delete currency with id: " + currencyModel.getId() + " from the database");
        }
    }

    public Optional<CurrencyModel> findByCode(String code) {
        final String FIND_BY_CODE_SQL = """
                SELECT * FROM currencies WHERE code = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL))
        {
            statement.setString(1, code);
            var resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to find currency with code: " + code + " from the database");
        }
    }

    private CurrencyModel buildCurrency(ResultSet resultSet) throws SQLException {
        return new CurrencyModel(
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
