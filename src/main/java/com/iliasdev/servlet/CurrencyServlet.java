package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.model.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().replaceAll("/", "");
        Currency currency = currencyDao.findByCode(code);
        objectMapper.writeValue(resp.getWriter(), currency);
    }
}
