package com.itu4061.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import com.itu4061.annotation.ApiRest;
import com.itu4061.annotation.ViewFile;
import com.itu4061.annotation.Controlleur;
import com.itu4061.annotation.GetUrl;
import com.itu4061.annotation.Param;
import com.itu4061.annotation.PostUrl;
import com.itu4061.exception.InnerRequestException;
import com.itu4061.map.MethodMapping;
import com.itu4061.utils.FrameworkUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class FrontController extends HttpServlet {
    public ArrayList<Class<?>> classeAnnoted = new ArrayList<Class<?>>();
    public ArrayList<String> allWebappClassName;

    public Map<String, MethodMapping<GetUrl>> urlGetMapping;
    public Map<String, MethodMapping<PostUrl>> urlPostMapping;
    public ArrayList<String> jspFileList;

    @Override
    public void init() throws ServletException {
        super.init();

        ClassLoader classLoader = getClassLoader();
        Package[] packages = classLoader.getDefinedPackages();

        allWebappClassName = getAllWebappClasses();
        urlGetMapping = urlGetControllerMapping();
        urlPostMapping = urlPostControllerMapping();
        jspFileList = new ArrayList<>();
        FrameworkUtils.scanForViewJsp(new File(getServletContext().getRealPath("")),
                getServletContext().getRealPath(""), jspFileList);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        processRequest(request, response, urlGetMapping);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        processRequest(request, response, urlPostMapping);

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, Map<?, ?> urlMap) {
        System.out.println(getRequestURI(request));
        MethodMapping methodMapping = null;
        try {
            methodMapping = (MethodMapping) urlMap.get(getRequestURI(request));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            return;
        }

        if (methodMapping == null && jspFileList.contains("/" + getRequestURI(request)) == false) {

            try {
                response.sendError(404, getRequestURI(request));
                System.out.println(getRequestURI(request));
                System.out.println(404);
                return;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getMessage() + " : " + e.getStackTrace());
            }
        }

        if (methodMapping != null) {
            Class<?> class1 = methodMapping.getSource();

            Object o = getBeanInstanceOf(class1);

            Method method = methodMapping.getMethod();

            Parameter[] params = method.getParameters();

            System.out.println(method.getName());
            for (int i = 0; i < params.length; i++) {
                if (!params[i].isAnnotationPresent(Param.class)) {

                    System.out.println(params[i].getName() + " : " + params[i].getType().getSimpleName());
                } else {
                    System.out.println(params[i].getAnnotation(Param.class).value() + " : " + params[i].getType().getSimpleName());
                }
            }

            Object[] args;
            try {
                args = getRequestParameters(request, params);
            } catch (InnerRequestException e) {
                try {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                } catch (java.io.IOException responseException) {
                    responseException.initCause(e);
                    throw new RuntimeException(responseException);
                }
                return;
            }

            
            if (method.isAnnotationPresent(ViewFile.class)) {
                try {
                    String view = method.getDeclaredAnnotation(ViewFile.class).view();
                    if (!jspFileList.contains(view)) {
                        System.out.println("erreur --> view : " + view + " inexistant");
                        return;
                    }
                    RequestDispatcher dispatcher = request.getRequestDispatcher(view);
                    Object rez = method.invoke(o,args);

                    request.setAttribute("return", rez);
                    System.out.println(view);
                    dispatcher.forward(request, response);

                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
            } else if (method.isAnnotationPresent(ApiRest.class)
                    || class1.getAnnotation(Controlleur.class).type().equals("REST")) {
                try {
                    Object rez = method.invoke(o,args);
                    Gson gson = new Gson();
                    String json = gson.toJson(rez);

                    response.setContentType("application/json");
                    response.getWriter().println(json);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            else {
                try {
                    method.invoke(o, args);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "eaast");
                }
            }
        }
    }

    private Object[] getRequestParameters(HttpServletRequest request, Parameter[] params)
            throws InnerRequestException {
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter parameter = params[i];
            Param param = parameter.getAnnotation(Param.class);
            if (param == null) {
                throw new InnerRequestException("Le parametre " + parameter.getName()
                        + " doit etre annote avec @Param");
            }

            String value = request.getParameter(param.value());
            if (value == null) {
                throw new InnerRequestException("Parametre de requete manquant : " + param.value());
            }

            args[i] = castRequestParameter(value, parameter.getType(), param.value());
        }

        return args;
    }

    private Object castRequestParameter(String value, Class<?> targetType, String parameterName)
            throws InnerRequestException {
        try {
            if (targetType == String.class) {
                return value;
            }
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.valueOf(value);
            }
            if (targetType == long.class || targetType == Long.class) {
                return Long.valueOf(value);
            }
            if (targetType == double.class || targetType == Double.class) {
                return Double.valueOf(value);
            }
            if (targetType == float.class || targetType == Float.class) {
                return Float.valueOf(value);
            }
            if (targetType == short.class || targetType == Short.class) {
                return Short.valueOf(value);
            }
            if (targetType == byte.class || targetType == Byte.class) {
                return Byte.valueOf(value);
            }
            if (targetType == boolean.class || targetType == Boolean.class) {
                if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("valeur booleenne invalide");
                }
                return Boolean.valueOf(value);
            }
            if (targetType == char.class || targetType == Character.class) {
                if (value.length() != 1) {
                    throw new IllegalArgumentException("un caractere est attendu");
                }
                return value.charAt(0);
            }
            if (targetType.isEnum()) {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                Object enumValue = Enum.valueOf((Class<? extends Enum>) targetType, value);
                return enumValue;
            }
        } catch (IllegalArgumentException e) {
            throw new InnerRequestException("Valeur invalide pour le parametre " + parameterName
                    + " (type attendu : " + targetType.getSimpleName() + ")", e);
        }

        throw new InnerRequestException("Type de parametre non supporte pour " + parameterName
                + " : " + targetType.getName());
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
                        System.out.println(e.getMessage());
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
                        System.out.println(e.getMessage());

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
                System.out.println(e.getMessage());
                return null;
            }
            context.setAttribute(classe.getName(), o);
            return o;
        }
    }
}
