package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.dto.ExchangeRatesRequestDto;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.CurrencyModel;
import com.iliasdev.model.ExchangeRates;
import com.iliasdev.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Currency;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();



    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String exchangeCodes = req.getPathInfo().replaceAll("/", "");

        if(exchangeCodes.length() != 6) {
            throw new InvalidParameterException("Currency codes provided in an incorrect format");
        }

        String baseCurrencyCode = exchangeCodes.substring(0,3);
        String targetCurrencyCode = exchangeCodes.substring(3);

        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);


        ExchangeRates exchangeRates = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Exchange  rate with codes " + baseCurrencyCode + " to " + targetCurrencyCode + " not found"));

        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String exchangeCodes = req.getPathInfo().replaceAll("/", "");
        String rate = req.getParameter("rate");

        if(exchangeCodes.length() !=6){
            throw new InvalidParameterException("Currency codes provided in an incorrect format");
        }

        if(rate == null || rate.isBlank()) {
            throw new InvalidParameterException("Missing parameter rate");
        }

        String baseCurrencyCode = exchangeCodes.substring(0,3);
        String targetCurrencyCode = exchangeCodes.substring(3);

        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);

        ExchangeRates exchangeRates = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Exchange rate with codes " + baseCurrencyCode + " to " + targetCurrencyCode + " not found"));

        exchangeRates.setRate(parseToDouble(rate));
        exchangeRatesDao.update(exchangeRates);
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    private static double parseToDouble(String rate) {
        try{
            double rate1 = Double.parseDouble(rate);
            if(rate1 < 0){
                throw new InvalidParameterException("Rate must be a non-negative number");
            }
            return rate1;
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}
