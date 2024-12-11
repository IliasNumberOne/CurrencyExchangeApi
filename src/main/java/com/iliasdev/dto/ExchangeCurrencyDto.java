package com.iliasdev.dto;

import com.iliasdev.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeCurrencyDto {
    private CurrencyModel baseCurrencyModel;
    private CurrencyModel targetCurrencyModel;
    private double rate;
    private double amount;
    private double convertedAmount;
}
