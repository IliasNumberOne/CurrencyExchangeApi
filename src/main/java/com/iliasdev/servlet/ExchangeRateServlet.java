package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.ExchangeRates;
import com.iliasdev.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String exchangeCodes = req.getPathInfo().replaceAll("/", "");
        String parameter = req.getReader().readLine();

        if(exchangeCodes.length() !=6){
            throw new InvalidParameterException("Currency codes provided in an incorrect format");
        }

        if(parameter == null || !parameter.contains("rate")) {
            throw new InvalidParameterException("Missing parameter rate");
        }
        String rate = parameter.replace("rate=", "");

        String baseCurrencyCode = exchangeCodes.substring(0,3);
        String targetCurrencyCode = exchangeCodes.substring(3);

        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);

        ExchangeRates exchangeRates = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Exchange rate with codes " + baseCurrencyCode + " to " + targetCurrencyCode + " not found"));

        exchangeRates.setRate(parseToBigDecimal(rate));
        exchangeRatesDao.update(exchangeRates);
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    private static BigDecimal parseToBigDecimal(String rate) {
        try{
            BigDecimal rate1 = BigDecimal.valueOf(Double.parseDouble(rate));
            if(rate1.compareTo(BigDecimal.ZERO) < 0){
                throw new InvalidParameterException("Rate must be a non-negative number");
            }
            return rate1;
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
        resp.setStatus(200);
    }
}
