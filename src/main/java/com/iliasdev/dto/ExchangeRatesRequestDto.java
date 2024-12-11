package com.iliasdev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRatesRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private double rate;
}
