package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.model.Currency;
import com.iliasdev.model.ExchangeRates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String exchangeCodes = req.getPathInfo().replaceAll("/", "");
        String baseCurrencyCode = exchangeCodes.substring(0,3);
        String targetCurrencyCode = exchangeCodes.substring(3);

        ExchangeRates exchangeRates = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode);

        objectMapper.writeValue(resp.getWriter(), exchangeRates);


        try(var printWriter = resp.getWriter()) {
            printWriter.write(baseCurrencyCode);
            printWriter.write(targetCurrencyCode);
        }

    }
}
