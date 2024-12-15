package com.iliasdev.dao;

import com.iliasdev.exception.DataBaseOperationException;
import com.iliasdev.exception.EntityExistException;
import com.iliasdev.exception.NotFoundException;
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

public class ExchangeRatesDao implements Dao<Integer, ExchangeRates>{
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Override
    public ExchangeRates create(ExchangeRates exchangeRates) {
        final String CREATE_SQL = """
                INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, exchangeRates.getBaseCurrency().getId());
            statement.setInt(2, exchangeRates.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRates.getRate());
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if(keys.next()) {
                exchangeRates.setId(keys.getInt(1));
            }

            return exchangeRates;
        } catch (SQLException e) {
            if(e instanceof PSQLException){
                PSQLException exception = (PSQLException) e;
                if(exception.getSQLState().equals("23505")){
                    throw new EntityExistException(
                            String.format(
                                    "Exchange rate '%s' to '%s' already exists",
                                    exchangeRates.getBaseCurrency().getCode(),
                                    exchangeRates.getTargetCurrency().getCode()));
                }
            }
            throw new DataBaseOperationException(
                    String.format("Failed to save exchange rates '%s' to '%s' to the database",
                            exchangeRates.getBaseCurrency().getCode(),
                            exchangeRates.getTargetCurrency().getCode())
            );
        }
    }

    @Override
    public Optional<ExchangeRates> findById(Integer id) {
        final String FIND_BY_ID = """
                SELECT * FROM exchange_rates WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID)) 
        {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(buildExchangeRates(resultSet));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to find exchange rates with id: " + id + "from the database");
        }
    }

    @Override
    public List<ExchangeRates> findAll() {
        final String FIND_ALL_SQL = """
                SELECT * FROM exchange_rates
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL))
        {
            var resultSet = statement.executeQuery();

            List<ExchangeRates> exchangeRatesList = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRatesList.add(buildExchangeRates(resultSet));
            }

            return exchangeRatesList;
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to find exchange rates from the database");
        }


    }

    @Override
    public ExchangeRates update(ExchangeRates exchangeRates) {
        final String UPDATE_SQL = """
                UPDATE exchange_rates
                SET base_currency_id = ?, target_currency_id = ?, rate = ?
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL))
        {
            statement.setInt(1, exchangeRates.getBaseCurrency().getId());
            statement.setInt(2, exchangeRates.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRates.getRate());
            statement.setInt(4, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to update exchange rates with id: " + exchangeRates.getId() + "from the database");
        }
        return exchangeRates;
    }

    @Override
    public void delete(ExchangeRates exchangeRates) {
        final String DELETE_SQL = """
                DELETE FROM exchange_rates
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL))
        {
            statement.setInt(1, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to delete exchange rates with id: " + exchangeRates.getId() + "from the database");
        }
    }

    public Optional<ExchangeRates> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
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
        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CODES_SQL))
        {
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(buildExchangeRates(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataBaseOperationException(
                    String.format("Failed to find exchange rates with codes '%s' to '%s' from the database",
                            baseCurrencyCode, targetCurrencyCode)
            );
        }
    }

    public Optional<ExchangeRates> findByCrossConvert(String baseCurrencyCode, String targetCurrencyCode) {
        final String FIND_BY_CROSS_CONVERT_SQL = """
                select er1.id as id,
                       baseCr1.id as base_currency_id,
                       baseCr2.id as target_currency_id,
                       round(er1.rate / er2.rate, 4) as rate
                from exchange_rates er1
                         join currencies baseCr1 on baseCr1.id = er1.base_currency_id
                         join exchange_rates er2 on er2.target_currency_id = er1.target_currency_id
                         join currencies baseCr2 on baseCr2.id = er2.base_currency_id
                         join currencies tc on tc.id = er1.target_currency_id
                where baseCr1.code = ?
                  and baseCr2.code = ?
                LIMIT 1;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CROSS_CONVERT_SQL))
        {
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(buildExchangeRates(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataBaseOperationException(
                    String.format("Failed to find exchange rates with cross convert '%s' to '%s' from the database",
                            baseCurrencyCode, targetCurrencyCode)
            );
        }
    }


    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        int baseCurrencyId = resultSet.getInt("base_currency_id");
        int targetCurrencyId = resultSet.getInt("target_currency_id");

        return new ExchangeRates(
                resultSet.getInt("id"),
                currencyDao.findById(baseCurrencyId)
                        .orElseThrow(() -> new NotFoundException("Currency with id " + baseCurrencyId + " not found" )),
                currencyDao.findById(targetCurrencyId)
                        .orElseThrow(() -> new NotFoundException("Currency with id " + targetCurrencyId + " not found" )),
                resultSet.getBigDecimal("rate")
        );
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }
}
