package com.itu.4061.controller;
import jakarta.servlet.*;
import jakarta.servlet.http.* ;
public class FrontController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
       processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String path = request.req.getServletPath();
        throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
    }
}