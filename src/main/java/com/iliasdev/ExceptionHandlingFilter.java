package com.iliasdev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasdev.dto.ErrorResponseDto;
import com.iliasdev.exception.DataBaseOperationException;
import com.iliasdev.exception.EntityExistException;
import com.iliasdev.exception.InvalidParameterException;
import com.iliasdev.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try{
            super.doFilter(req, res, chain);
        }
        catch(DataBaseOperationException e){
            writeErrorResponse(res, SC_INTERNAL_SERVER_ERROR, e);
        }
        catch(EntityExistException e){
            writeErrorResponse(res, SC_CONFLICT, e);
        }
        catch(InvalidParameterException e){
            writeErrorResponse(res, SC_BAD_REQUEST, e);
        }
        catch(NotFoundException e){
            writeErrorResponse(res, SC_NOT_FOUND, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse res, int errorCode, RuntimeException errorMessage) throws IOException {
        res.setStatus(errorCode);

        objectMapper.writeValue(res.getWriter(), new ErrorResponseDto(errorCode, errorMessage.getMessage()));
    }
}
