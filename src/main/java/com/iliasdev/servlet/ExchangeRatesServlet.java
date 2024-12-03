package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRates> exchangeRatesList = exchangeRatesDao.findAll();
        objectMapper.writeValue(resp.getWriter(), exchangeRatesList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        final String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        final String rate = req.getParameter("rate");


        if(baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || rate.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode);
        double rateDouble = Double.parseDouble(rate);

        if(baseCurrency == null || targetCurrency == null) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.sendError(1, "One or both of currency codes don't exist");
        }

        ExchangeRates exchangeRates = exchangeRatesDao.create(new ExchangeRates(baseCurrency, targetCurrency, rateDouble));
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }
}
