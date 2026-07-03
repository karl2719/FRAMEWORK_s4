package com.itu4061.map;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class MethodMapping < A extends Annotation > {
    private Method method ;
    private Class<?> source ;
    private A annotationA ;
    public MethodMapping() {
    }

    
    public MethodMapping(Method method, Class<?> source, A annotationA) {
        this.method = method;
        this.source = source;
        this.annotationA = annotationA;
    }


    public Method getMethod() {
        return method;
    }


    public Class<?> getSource() {
        return source;
    }


    public A getAnnotationA() {
        return annotationA;
    }

    
}
