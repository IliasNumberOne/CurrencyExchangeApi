package com.iliasdev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeCurrencyRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal amount;
}
