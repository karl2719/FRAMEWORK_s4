package com.itu4061.map;

import java.lang.reflect.Method;

import com.itu4061.exception.FrameworkException4061;
import com.itu4061.exception.MappingMethodException;

import java.lang.annotation.Annotation;

public class MethodMapping < A extends Annotation > {
    private Method method ;
    private Class<?> source ;
    private A annotationA ;
    public MethodMapping() {
    }

    
    public MethodMapping(Method method, Class<?> source, A annotation) throws FrameworkException4061{
        this.method = method;
        this.source = source;
        try {
            this.annotationA = annotation;
            
        } catch (Exception e) {
            throw new MappingMethodException("Type samy hafa :" + this.annotationA.getClass().getTypeName() + "sy " + annotation.getClass().getTypeName() );
        }
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
