package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

public class TestFrontController extends HttpServlet{
    public ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();

    @Override
    public void init() throws ServletException {
        super.init();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        String packageName = this.getClass().getPackage().getName();

        String path = packageName.replace('.', '/');

        URL resource = classLoader.getResource(path);
        File directory = new File(resource.getFile());

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
        
    }
    private ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader() ;
    }
}
