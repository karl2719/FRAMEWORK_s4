package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import com.itu4061.annotation.Controlleur;
import com.itu4061.annotation.Entite;
import com.itu4061.annotation.GetUrl;
import com.itu4061.annotation.PostUrl ;
import com.itu4061.annotation.ContolMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class FrontController extends HttpServlet {
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();
    public ArrayList<String> allWebappClassName;
    public Map<String, Method> methodPostMapping ;
    public Map<String, Method> methodGetMapping ;

    
    @Override
    public void init() throws ServletException {
        super.init();

        ClassLoader classLoader = getClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        allWebappClassName = getAllWebappClasses();
        methodPostMapping = getPostUrlController() ;
        methodGetMapping = getGetUrlController();
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


        for (String c : this.getAllWebappClasses()) {
            out.println(c + "</br>");
        }
        out.println(getAnnotatedClassesBy(Entite.class).toString());
        getAllAnnotated(response, Controlleur.class);
        out.println(getGetUrlController());
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
            Class<?> cls = null;

            try {
                cls = Class.forName(name);
            } catch (Exception e) {
                System.out.println(e.getMessage() + " : ClassNotFound => ");
                continue;
            }

            if (cls != null) {
                Annotation ann = null;
                try {
                    ann = cls.getAnnotation(annotationClass);
                } catch (Exception e) {
                }

                if (ann != null) {
                    out.println("</br> <h2>" + name + "</h2></br>");
                    out.println("<h3>Is annotated by : <h3>" + ann.annotationType() + "</br> ");
                    out.println(Arrays.toString(ann.annotationType().getDeclaredMethods()));
                    for (Method m : ann.annotationType().getDeclaredMethods()) {
                        try {
                            Object o = m.invoke(ann);
                            out.println("<h4>" + m.toString() + " : " + o.toString() + "</h4> </br>");

                        } catch (Exception e) {
                            continue;
                        }
                    }
                } else {
                    out.println(
                            "</br>" + cls.getName() + "is not annotated by :" + annotationClass.getName() + "</br>");
                }

            } else {

            }

        }
    }

    private ArrayList<Class<?>> getAnnotatedClassesBy(Class<? extends Annotation> annotation) {
        ArrayList<Class<?>> rez = new ArrayList<Class<?>>();
        ArrayList<String> webappClasses = getAllWebappClasses();

        for (String name : webappClasses) {
            Class<?> cls = null;

            try {
                cls = Class.forName(name, false, getClassLoader());
            } catch (Exception e) {
                System.out.println(e.getMessage() + " : ClassNotFound => ");
                continue;
            }

            if (cls != null) {
                Annotation ann = null;
                try {
                    ann = cls.getAnnotation(annotation);
                } catch (Exception e) {
                }

                if (ann != null) {
                    rez.add(cls);
                } else {
                    System.out.println(
                            "</br>" + cls.getName() + "is not annotated by :" + annotation.getName() + "</br>");
                }

            } else {

            }
        }
        return rez;
    }

    private Map<String, Method> getGetUrlController() {
        Map<String, Method> rez = new HashMap<>();
        ArrayList<Class<?>> controllers = getAnnotatedClassesBy(Controlleur.class);
        for (Class<?> cont : controllers) {
            for (Method m : cont.getMethods()) {
                if (m.isAnnotationPresent(GetUrl.class)) {

                    GetUrl w = m.getAnnotation(GetUrl.class);
                    cont.getAnnotationsByType(ContolMapping.class);
                    try {
                        rez.put(w.url(), m);
                    } catch (Exception e) {
                    }
                }
            }
        }

        return rez;
    }
    private Map<String, Method> getPostUrlController() {
        Map<String, Method> rez = new HashMap<>();
        ArrayList<Class<?>> controllers = getAnnotatedClassesBy(Controlleur.class);
        for (Class<?> cont : controllers) {
            for (Method m : cont.getMethods()) {
                if (m.isAnnotationPresent(PostUrl.class)) {

                    GetUrl w = m.getAnnotation(GetUrl.class);
                    cont.getAnnotationsByType(ContolMapping.class);
                    try {
                        rez.put(w.url(), m);
                    } catch (Exception e) {
                    }
                }
            }
        }

        return rez;
    }
    public Map<String, Method> getMethodPostMapping() {
        return methodPostMapping;
    }

    public Map<String, Method> getMethodGetMapping() {
        return methodGetMapping;
    }

}
