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
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencyList = currencyDao.findAll();
        objectMapper.writeValue(resp.getWriter(), currencyList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || name.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);

            return;
        }
        if (code == null || code.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);

            return;
        }
        if (sign == null || sign.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);

            return;
        }


        Currency currency = currencyDao.create(new Currency(code, name, sign));
        objectMapper.writeValue(resp.getWriter(), currency);
    }
}
