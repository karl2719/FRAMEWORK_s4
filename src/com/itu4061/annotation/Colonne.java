package com.itu4061.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//test package
import com.itu4061.annotation.caracteristique.Caracteristique;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Colonne {
    String name() ; 
    Caracteristique type() ;
}
