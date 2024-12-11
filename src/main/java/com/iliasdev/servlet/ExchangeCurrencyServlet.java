package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.dto.ExchangeCurrencyDto;
import com.iliasdev.dto.ExchangeCurrencyRequestDto;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.*;
import com.iliasdev.service.ExchangeCurrencyService;
import com.iliasdev.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeCurrencyServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private static final ExchangeCurrencyService exchangeCurrencyService = ExchangeCurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String baseCurrencyCode = req.getParameter("from");
        final String targetCurrencyCode = req.getParameter("to");
        final String amount = req.getParameter("amount");

        if(amount == null || amount.isBlank()) {
            throw new InvalidParameterException("Missing parameter amount");
        }

        ExchangeCurrencyRequestDto exchangeCurrencyRequestDto = new ExchangeCurrencyRequestDto(baseCurrencyCode, targetCurrencyCode, parseAmount(amount));
        ValidationUtil.validate(exchangeCurrencyRequestDto);


        CurrencyModel baseCurrencyModel = currencyDao.findByCode(baseCurrencyCode).orElseThrow(() -> new NotFoundException("Currency with code " + baseCurrencyCode + " not found"));
        CurrencyModel targetCurrencyModel = currencyDao.findByCode(targetCurrencyCode).orElseThrow(() -> new NotFoundException("Currency with code " + targetCurrencyCode + " not found"));

        ExchangeCurrencyDto exchangeCurrencyDto = exchangeCurrencyService.exchangeCurrency(baseCurrencyModel, targetCurrencyModel, parseAmount(amount));

        objectMapper.writeValue(resp.getWriter(), exchangeCurrencyDto);
    }

    private static double parseAmount(String amountPam) {
        try{
            return Double.parseDouble(amountPam);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter amount must be a number");
        }
    }
}
