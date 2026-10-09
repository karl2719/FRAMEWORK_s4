package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.View;

import com.google.gson.Gson;

import com.itu4061.annotation.ApiRest;
import com.itu4061.annotation.ViewFile;
import com.itu4061.annotation.Controlleur;
import com.itu4061.annotation.GetUrl;
import com.itu4061.annotation.PostUrl;
import com.itu4061.map.MethodMapping;
import com.itu4061.map.Model;
import com.itu4061.utils.FrameworkUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TestFrontController extends HttpServlet {
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();
    public ArrayList<String> allWebappClassName;

    public Map<String, MethodMapping<GetUrl>> urlGetMapping;
    public Map<String, MethodMapping<PostUrl>> urlPostMapping;
    public ArrayList<String> jspFileList ;


    @Override
    public void init() throws ServletException {
        super.init();

        ClassLoader classLoader = getClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        allWebappClassName = getAllWebappClasses();
        urlGetMapping = urlGetControllerMapping();
        urlPostMapping = urlPostControllerMapping();
        jspFileList = new ArrayList<>() ;
        FrameworkUtils.scanForViewJsp(new File(getServletContext().getRealPath("")), getServletContext().getRealPath(""), jspFileList);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (Exception e) {

        }

        MethodMapping methodMapping = null;
        try {
            methodMapping = urlGetMapping.get(getRequestURI(request));
        } catch (Exception e) {
            out.println(e.getMessage());
            return;
        }

        if (methodMapping == null) {
            out.println(404);
            return;
        }
        Class<?> class1 = methodMapping.getSource();

        Object o = getBeanInstanceOf(class1);

        Method method = methodMapping.getMethod();
        if (method.isAnnotationPresent(ApiRest.class)) {
            try {
                Object rez = method.invoke(o);
                Gson gson = new Gson();
                String json = gson.toJson(rez);
                
                response.setContentType("application/json");

                out.println(gson.toJson(json));
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } 
        else if (method.isAnnotationPresent(ViewFile.class)){
            try {
                String view = method.getDeclaredAnnotation(ViewFile.class).view();
                if (!jspFileList.contains(view)) {
                    out.println("erreur --> view : " + view + " inexistant") ;
                    return ;
                }
                RequestDispatcher dispatcher =  request.getRequestDispatcher(view);
                dispatcher.forward(request, response);
                
            } catch (Exception e) {

            }
        }
        else {
            try {
                method.invoke(o);
            } catch (Exception e) {
                out.println(e.getMessage() + "eaast");
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (Exception e) {

        }

         MethodMapping methodMapping = null;
        try {
            methodMapping = urlPostMapping.get(getRequestURI(request));
        } catch (Exception e) {
            out.println(e.getMessage());
            return;
        }

        if (methodMapping == null) {
            out.println(404);
            return;
        }
        Class<?> class1 = methodMapping.getSource();

        Object o = getBeanInstanceOf(class1);

        Method method = methodMapping.getMethod();
        if (method.isAnnotationPresent(ApiRest.class)) {
            try {
                Model rez = (Model)method.invoke(o);
                Gson gson = new Gson();
                String json = gson.toJson(rez);
                response.setContentType("application/json");
                out.println(json);
            } catch (Exception e) {

            }
        } else {
            try {
                method.invoke(o);
            } catch (Exception e) {
                out.println(e.getMessage() + "eaast");
            }
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {

    }

    private String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI().replace(request.getContextPath() + "/", "");
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

    private void getAllAnnotated(HttpServletResponse response,
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

    private Map<String, MethodMapping<GetUrl>> urlGetControllerMapping() {
        Map<String, MethodMapping<GetUrl>> rez = new HashMap<>();
        ArrayList<Class<?>> controllers = getAnnotatedClassesBy(Controlleur.class);

        for (Class<?> cont : controllers) {
            Controlleur src = cont.getAnnotation(Controlleur.class);

            for (Method m : cont.getMethods()) {
                if (m.isAnnotationPresent(GetUrl.class)) {

                    GetUrl getUrl = m.getAnnotation(GetUrl.class);
                    String key = src.mapping() + getUrl.url();
                    try {
                        MethodMapping methodMapping = new MethodMapping<Annotation>(m, cont, getUrl);
                        rez.put(key, methodMapping);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return rez;
    }

    private Map<String, MethodMapping<PostUrl>> urlPostControllerMapping() {
        Map<String, MethodMapping<PostUrl>> rez = new HashMap<>();
        ArrayList<Class<?>> controllers = getAnnotatedClassesBy(Controlleur.class);

        for (Class<?> cont : controllers) {
            Controlleur src = cont.getAnnotation(Controlleur.class);

            for (Method m : cont.getMethods()) {
                if (m.isAnnotationPresent(PostUrl.class)) {

                    PostUrl postUrl = m.getAnnotation(PostUrl.class);
                    String key = src.mapping() + postUrl.url();
                    try {
                        MethodMapping methodMapping = new MethodMapping<Annotation>(m, cont, postUrl);
                        rez.put(key, methodMapping);
                    } catch (Exception e) {

                    }
                }
            }
        }
        return rez;
    }

    private Object getBeanInstanceOf(Class<?> classe) {
        ServletContext context = this.getServletContext();
        Object o = null;
        o = context.getAttribute(classe.getName());
        if (o != null) {
            return o;
        } else {
            try {
                o = classe.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
            context.setAttribute(classe.getName(), o);
            return o;
        }
    }
}
