package com.iliasdev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRates {
    private int id;
    private CurrencyModel baseCurrency;
    private CurrencyModel targetCurrency;
    private double rate;

    public ExchangeRates(CurrencyModel baseCurrency, CurrencyModel targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
