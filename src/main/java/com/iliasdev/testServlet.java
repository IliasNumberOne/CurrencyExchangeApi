package com.iliasdev;

import com.iliasdev.util.ConnectionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/")
public class testServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        try(var printWriter = resp.getWriter()) {
            printWriter.write("<h1>Tadadadadada</h1>");
            printWriter.write("<h1>Id of currencies: </h1>");
            printWriter.write("<ul>");
            getAllCurrency().stream().forEach(id -> printWriter.write("<li>" + id + "</li>"));
            printWriter.write("</ul>");
        }
    }

    private static List<Integer> getAllCurrency() {
        String sql = "Select * from currencies";

        List<Integer> list = new ArrayList<Integer>();

        try(var connection = ConnectionManager.open();
            var preparedStatement = connection.prepareStatement(sql))
        {
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getObject("id", Integer.class));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
