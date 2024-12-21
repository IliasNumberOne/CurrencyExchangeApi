package com.iliasdev.util;


import com.iliasdev.dto.ExchangeCurrencyRequestDto;
import com.iliasdev.dto.ExchangeRatesRequestDto;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.model.CurrencyModel;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {
    private static Set<String> currencyCodes;

    public static void validate(CurrencyModel currencyModel) {
        String code = currencyModel.getCode();
        String name = currencyModel.getFullName();
        String sign = currencyModel.getSign();

        if(code == null || code.isBlank()) {
            throw new InvalidParameterException("Missing parameter currency code");
        }
        if(name == null || name.isBlank()) {
            throw new InvalidParameterException("Missing parameter currency name");
        }
        if(sign == null || sign.isBlank()) {
            throw new InvalidParameterException("Missing parameter currency sign");
        }

        validateCurrencyCode(code);
    }

    public static void validate(ExchangeRatesRequestDto exchangeRatesRequestDto) {
        String baseCurrencyCode = exchangeRatesRequestDto.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRatesRequestDto.getTargetCurrencyCode();
        BigDecimal rate = exchangeRatesRequestDto.getRate();

        if(baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter base currency code");
        }
        if(targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter target currency code");
        }
        if(rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidParameterException("Invalid parameter - rate must be non-negative");
        }

        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);

    }

    public static void validate(ExchangeCurrencyRequestDto exchangeCurrencyRequestDto) {
        String baseCurrencyCode = exchangeCurrencyRequestDto.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeCurrencyRequestDto.getTargetCurrencyCode();
        BigDecimal amount = exchangeCurrencyRequestDto.getAmount();

        if(baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter base currency code");
        }
        if(targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter target currency code");
        }
        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidParameterException("Invalid parameter - amount must be non-negative or zero");
        }

        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
    }

    public static void validateCurrencyCode(String code) {
        if (code.length() != 3) {
            throw new InvalidParameterException("Currency code must contain exactly 3 letters");
        }


        if (currencyCodes == null) {
            Set<Currency> currencies = Currency.getAvailableCurrencies();
            currencyCodes = currencies.stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());
        }

        if (!currencyCodes.contains(code)) {
            throw new InvalidParameterException("Currency code must be in ISO 4217 format");
        }
    }

}
