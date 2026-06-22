package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.URL;

public class TestFrontController extends HttpServlet {
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();
    // public Map<Annotation,Class<?>> ClassAnnoatationMappe = new HashMap<>();

    @Override
    public void init() throws ServletException{
        super.init();

        ClassLoader classLoader = getClassLoader();
        // recuperation des packages
        Package[] packages = classLoader.getDefinedPackages();


        ArrayList<Class<?>> webAppClasses = new ArrayList<>() ;
        try {
            this.updateWebappclasses() ;
        } catch (Exception e) {
            throw new ServletException(e.getMessage()) ;
        }
        // String packageName = this.getClass().getPackage().getName();
        // String path = packageName.replace('.', '/');
        // URL resource = classLoader.getResource(path);
        // File directory = new File(resource.getFile());

    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        String packageName = this.getClass().getPackage().getName();

        String path = packageName.replace('.', '/');

        URL resource = classLoader.getResource(path);
        File directory = new File(resource.getFile());

        response.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();

            out.println("<h1>Current Thread</h1>" + getClassLoader().toString() + "\n </br>");

            out.println("<h1>Directory file</h1>");

            for (File file : directory.listFiles()) {
                out.println(file.getName());
            }

            out.println("</br> <h1>Package list</h1>");
            for (Package pack : packages) {
                out.println(pack.getName() + "</br>");
            }

            out.println("</br> <h1>ClassLoaded</h1>");
            for (Package pack : packages) {
                if (!pack.getName().contains("com")) {
                    String relativePath = pack.getName().replace('.', '/');
                    URL packRessources = classLoader.getResource(relativePath);
                    File directoryPack = new File(packRessources.getFile());
                    out.println("<h2>" + pack.getName() + "</h2>" + "<ul>");

                    for (File classLoaded : directoryPack.listFiles()) {
                        String classname = pack.getName() + "." + classLoaded.getName().replace(".class", "");
                        webAppClasses.add(Class.forName(classname));
                        out.println("<li>" + classname + "</li>" + "</br>");
                    }
                    out.println("</ul>");
                } else {
                    continue;
                }
            }
            out.println("<h2>Liste des annoations par classes</h2>");
            for (Class<?> cls : webAppClasses) {
                Annotation[] anno = cls.getAnnotations();
                out.println("<h3>" + cls.getName() + "</h3>");
                out.println("<ul>");
                for (Annotation a : anno) {
                    out.println("<li>" + a.toString() + "</li>");
                }
                out.println("</ul>");
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {

    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public ArrayList<Class<?>> updateWebappclasses() throws Exception{
        ClassLoader classLoader = getClassLoader();

        Package[] packages = classLoader.getDefinedPackages();
        ArrayList<Class<?>> webAppClasses = new ArrayList<Class<?>>();

         for (Package pack : packages) {
            if (!pack.getName().contains("com")) {
                String relativePath = pack.getName().replace('.', '/');
                URL packRessources = classLoader.getResource(relativePath);
                File directoryPack = new File(packRessources.getFile());

                for (File classLoaded : directoryPack.listFiles()) {
                    String classname = pack.getName() + "." + classLoaded.getName().replace(".class", "");
                    try {
                        webAppClasses.add(Class.forName(classname));                        
                    } catch (Exception e) {
                       throw new Exception("Class not found : "+classname);
                    }
                }
            } else {
                continue;
            }
        }
        return webAppClasses ;

    }
}
