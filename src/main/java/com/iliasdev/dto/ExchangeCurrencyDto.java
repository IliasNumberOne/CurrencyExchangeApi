package com.iliasdev.dto;

import com.iliasdev.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeCurrencyDto {
    private CurrencyModel baseCurrencyModel;
    private CurrencyModel targetCurrencyModel;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
