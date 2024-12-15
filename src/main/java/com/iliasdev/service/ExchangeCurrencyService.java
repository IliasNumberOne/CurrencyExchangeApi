package com.iliasdev.service;

import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.dto.ExchangeCurrencyDto;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.*;
import com.iliasdev.model.CurrencyModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class ExchangeCurrencyService {
    private static final ExchangeCurrencyService INSTANCE = new ExchangeCurrencyService();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

    public ExchangeCurrencyDto exchangeCurrency(CurrencyModel baseCurrencyModel, CurrencyModel targetCurrencyModel, BigDecimal amount) {
        ExchangeRates exchangeRates = getExchangeRate(baseCurrencyModel.getCode(), targetCurrencyModel.getCode())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Exchange rate '%s' - '%s' not found in the database",
                                baseCurrencyModel.getCode(), targetCurrencyModel.getCode()
                        )));

        BigDecimal convertedAmount = amount.multiply(exchangeRates.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        return new ExchangeCurrencyDto(
                exchangeRates.getBaseCurrency(),
                exchangeRates.getTargetCurrency(),
                exchangeRates.getRate(),
                amount,
                convertedAmount
        );
    }

    private Optional<ExchangeRates> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode);

        if(exchangeRates.isEmpty()) {
            exchangeRates = getReverseExchangeRate(baseCurrencyCode, targetCurrencyCode);
        }

        if(exchangeRates.isEmpty()) {
            exchangeRates = exchangeRatesDao.findByCrossConvert(baseCurrencyCode, targetCurrencyCode);
        }

        return exchangeRates;
    }

    private Optional<ExchangeRates> getReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.findByCodes(targetCurrencyCode, baseCurrencyCode);

        if (exchangeRates.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal rate = BigDecimal.ONE.divide(exchangeRates.get().getRate(), MathContext.DECIMAL64)
                .setScale(6, RoundingMode.HALF_UP);

        ExchangeRates directExchangeRates = new ExchangeRates(
                exchangeRates.get().getTargetCurrency(),
                exchangeRates.get().getBaseCurrency(),
                rate
        );
        return Optional.of(directExchangeRates);
    }


    public static ExchangeCurrencyService getInstance() {return INSTANCE;}


    /*---Method to find rate by reverse rate with basic crud operations---*/
    /*
    public ExchangeRates reverseExchangeCurrencyRate(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRates> exchangeRatesList = exchangeRatesDao.findAll();


        Map<String, Integer> codeCounts = exchangeRatesList.stream()
                .filter(exchangeRates ->
                        exchangeRates.getBaseCurrency().getCode().equals(baseCurrencyCode)
                        || exchangeRates.getBaseCurrency().getCode().equals(targetCurrencyCode))
                .map(exchangeRates -> exchangeRates.getTargetCurrency().getCode())
                .collect(Collectors.toMap(
                        code -> code,
                        code -> 1,
                        Integer::sum
                ));

        String crossCurrencyCode = codeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        System.out.println("codes and their counts: " + codeCounts);
        System.out.println("repetitive target currency: " + crossCurrencyCode);

        Currency crossCurrency = currencyDao.findByCode(crossCurrencyCode);
        double baseRate = exchangeRatesDao.findByCodes(baseCurrencyCode, crossCurrencyCode).getRate();
        double targetRate = exchangeRatesDao.findByCodes(targetCurrencyCode, crossCurrencyCode).getRate();
        double crossRate = baseRate / targetRate;
        System.out.println(baseCurrencyCode + " to " + targetCurrencyCode + ": " + crossRate);

        return new ExchangeRates(
                currencyDao.findByCode(baseCurrencyCode),
                currencyDao.findByCode(targetCurrencyCode),
                crossRate
        );
    }

     */

}
