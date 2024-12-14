package com.iliasdev;

import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.model.ExchangeRates;
import com.iliasdev.service.ExchangeCurrencyService;
import com.iliasdev.util.ConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class main {
    public static void main(String[] args) {
        ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

        Optional<ExchangeRates> result= exchangeRatesDao.findById(1);
        System.out.println("result: "+result);

    }
}
