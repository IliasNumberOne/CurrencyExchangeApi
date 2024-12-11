package com.iliasdev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeCurrencyRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private double amount;
}
