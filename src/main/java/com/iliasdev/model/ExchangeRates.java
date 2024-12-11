package com.iliasdev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRates {
    private int id;
    private CurrencyModel baseCurrencyModel;
    private CurrencyModel targetCurrencyModel;
    private double rate;

    public ExchangeRates(CurrencyModel baseCurrencyModel, CurrencyModel targetCurrencyModel, double rate) {
        this.baseCurrencyModel = baseCurrencyModel;
        this.targetCurrencyModel = targetCurrencyModel;
        this.rate = rate;
    }
}
