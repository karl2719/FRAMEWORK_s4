package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.itu4061.annotation.Controlleur;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestFrontController extends HttpServlet {
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();
    // public Map<Annotation,Class<?>> ClassAnnoatationMappe = new HashMap<>();
    public ArrayList<String> allWebappClassName;

    @Override
    public void init() throws ServletException {
        super.init();

        ClassLoader classLoader = getClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        allWebappClassName = getAllWebappClasses();

        // String packageName = this.getClass().getPackage().getName();
        // String path = packageName.replace('.', '/');
        // URL resource = classLoader.getResource(path);
        // File directory = new File(resource.getFile());

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (Exception e) {

        }

        // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // Package[] packages = classLoader.getDefinedPackages();
        // String packageName = this.getClass().getPackage().getName();
        // String path = packageName.replace('.', '/');
        // URL resource = classLoader.getResource(path);
        // File directory = new File(resource.getFile());
        // out.println("<h1>Current Thread</h1>" + getClassLoader().toString() + "\n
        // </br>");
        // out.println("<h1>Directory file</h1>");
        // for (File file : directory.listFiles()) {
        // out.println(file.getName());
        // }
        // out.println("</br> <h1>Package list</h1>");
        // for (Package pack : packages) {
        // out.println(pack.getName() + "</br>");
        // }

        for (String c : this.getAllWebappClasses()) {
            out.println(c + "</br>");
        }

        // getAllAnnotatedClassBy(response, Controlleur.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {

    }

    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public ArrayList<Class<?>> getLoadedWebappClasses() throws Exception {
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
                        throw new Exception("Class not found : " + classname);
                    }
                }
            } else {
                continue;
            }
        }
        return webAppClasses;

    }

    public ArrayList<String> getAllWebappClasses() {

        ClassLoader loader = getClassLoader();
        String path = loader.getResource("").getPath();
        ArrayList<String> result = new ArrayList<>();
        scanForClasses(new File(loader.getResource("").getFile()), path, result);

        return result;
    }

    private void scanForClasses(File dir, String root, ArrayList<String> result) {

        for (File file : dir.listFiles()) {

            if (file.isDirectory()) {
                scanForClasses(file, root, result);
            } else if (file.getName().endsWith(".class")) {
                String classname = file.getAbsolutePath()
                        .replace(root, "")
                        .replace(File.separator, ".")
                        .replace(".class", "");
                result.add(classname);
            }
        }
    }

    private /* ArrayList<Class<?>> */ void getAllAnnotated(HttpServletResponse response,
            Class<? extends Annotation> annotationClass) {
        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (Exception e) {

        }

        ArrayList<String> webappClasses = getAllWebappClasses();

        for (String name : webappClasses) {
            Class<?> annoted = null;

            try {
                annoted = Class.forName(name);
            } catch (Exception e) {
                out.println(e.getMessage() + " : ClassNotFound => ");
                continue;
            }
            Annotation ann = null;
            try {
                ann = annoted.getAnnotation(annotationClass);
            } catch (Exception e) {
                continue;
            }
            out.println(name + "</br>");
            out.println(ann.annotationType() + "</br>");
            out.println(
                    Arrays.toString(ann.annotationType().getDeclaredMethods()));
            for (Method m : ann.annotationType().getDeclaredMethods()) {
                try {
                    Object o = m.invoke(ann);
                    out.println(o.toString());

                } catch (Exception e) {
                    // continue;
                }
            }
        }
    }

    private ArrayList<Class<?>> getAnnotatedClassesBy(Class<? extends Annotation> annotation) {
        ArrayList<Class<?>> rez = new ArrayList<Class<?>>() ;
        ArrayList<String> webappClasses = getAllWebappClasses();

        for (String name : webappClasses) {
            Class<?> annoted = null;
            try {
                annoted = Class.forName(name);
                
            } catch (Exception e) {
                System.err.println("Class not found ");
                e.printStackTrace();
                continue ;
            }
        }

        return  rez;
    }
}
