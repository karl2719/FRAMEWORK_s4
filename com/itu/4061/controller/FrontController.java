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
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>test</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Bonjour depuis une Servlet !</h1>");
        out.println(path);
        out.println("</body>");
        out.println("</html>");
        throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
    }
}