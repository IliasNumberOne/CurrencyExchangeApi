package com.iliasdev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dao.CurrencyDao;
import com.iliasdev.exception.NotFoundException;
import com.iliasdev.model.CurrencyModel;
import com.iliasdev.util.ValidationUtil;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().replaceAll("/", "");
        ValidationUtil.validateCurrencyCode(code);

        CurrencyModel currencyModel = currencyDao.findByCode(code).orElseThrow(() -> new NotFoundException("Currency not found"));

        objectMapper.writeValue(resp.getWriter(), currencyModel);
    }
}
