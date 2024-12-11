package com.iliasdev.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyModel {
    private int id;
    private String code;
    @JsonProperty("name")
    private String fullName;
    private String sign;


    public CurrencyModel(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
