package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.dto.ExchangeRatesRequestDto;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.*;
import com.iliasdev.util.ValidationUtil;
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

        if(rate == null || rate.isBlank()) {
            throw new InvalidParameterException("Missing parameter rate");
        }

        ExchangeRatesRequestDto exchangeRatesRequestDto = new ExchangeRatesRequestDto(baseCurrencyCode, targetCurrencyCode, parseToDouble(rate));
        ValidationUtil.validate(exchangeRatesRequestDto);

        CurrencyModel baseCurrencyModel = currencyDao.findByCode(baseCurrencyCode).orElseThrow(() -> new NotFoundException("Currency with code " + baseCurrencyCode + " not found"));
        CurrencyModel targetCurrencyModel = currencyDao.findByCode(targetCurrencyCode).orElseThrow(() -> new NotFoundException("Currency with code " + targetCurrencyCode + " not found"));
        double rateDouble = parseToDouble(rate);


        ExchangeRates exchangeRates = exchangeRatesDao.create(new ExchangeRates(baseCurrencyModel, targetCurrencyModel, rateDouble));

        resp.setStatus(SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    private static double parseToDouble(String rate) {
        try{
            return Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}
