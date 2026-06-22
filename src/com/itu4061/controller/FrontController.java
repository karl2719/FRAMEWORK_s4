package com.itu4061.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class FrontController extends HttpServlet {

    protected Map<String, Class<?>> classAnnote;
    protected Package[] packagesloaded;
    @Override
    public void init() throws ServletException {
        super.init();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        packagesloaded = classLoader.getDefinedPackages();


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer url = request.getRequestURL();
        response.setContentType("text/html;charset=UTF-8");

        try {
            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>test</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>GET ALL URL : </h1>" + url.toString() + "</br>" );
            out.println(Thread.currentThread().getContextClassLoader().toString() );
            out.println("<ul>");
            for (Package pack : packagesloaded) {
                out.println(pack.getName());
                out.println("</br>");
            }
            out.println("</ul>");
        
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
        }
    }
}