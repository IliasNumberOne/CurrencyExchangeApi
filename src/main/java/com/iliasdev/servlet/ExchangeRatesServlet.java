package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.ExchangeRatesDao;
import com.iliasdev.model.ExchangeRates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRates> exchangeRatesList = exchangeRatesDao.findAll();
        objectMapper.writeValue(resp.getWriter(), exchangeRatesList);
    }
}
