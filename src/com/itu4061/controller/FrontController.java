package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.itu4061.annotation.Controlleur;
import com.itu4061.annotation.GetUrl;
import com.itu4061.annotation.PostUrl;
import com.itu4061.map.MethodMapping;

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

    public Map<String, MethodMapping<GetUrl>> urlGetMapping;
    public Map<String, MethodMapping<PostUrl>> urlPostMapping;

    @Override
    public void init() throws ServletException {
        super.init();
        allWebappClassName = getAllWebappClasses();
        urlGetMapping = urlGetControllerMapping();
        urlPostMapping = urlPostControllerMapping();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (Exception e) {

        }

        // initialisation de la methode mapping contenant la methode a invoquer et la
        // classe source de cette methode
        MethodMapping<GetUrl> methodMapping = null;
        try {
            methodMapping = urlGetMapping.get(getRequestURI(request));
        } catch (Exception e) {
            out.println(e.getMessage());
            return;
        }

        // Si la methode mapping est null, on retourne un code 404
        if (methodMapping == null) {
            out.println(404);
            return;
        }

        Class<?> class1 = methodMapping.getSource();
        
        // initialisation de l'objet de la classe source de la methode a invoquer
        Object o = null;
        try {
            o = class1.getDeclaredConstructor()
                    .newInstance();

        } catch (Exception e) {
            out.println(e.getMessage());
        }

        // initialisation de la methode a invoquer
        Method method = methodMapping.getMethod();
        try {
            method.invoke(o, request, response);
        } catch (Exception e) {
            out.println(e.getMessage() + "erreur lors de l'invocation de la methode " + method.getName() + " de la classe " + class1.getName());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
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

}
