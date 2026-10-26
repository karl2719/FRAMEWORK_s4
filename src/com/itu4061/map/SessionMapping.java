package com.itu4061.map;

import java.util.Enumeration;
import java.util.HashMap;


import jakarta.servlet.http.HttpSession;

public class SessionMapping {
    public String id ; 
    private HashMap<String , Object > parametters = new HashMap<>();

    public SessionMapping ( HttpSession session ){
        this.id=session.getId() ;
        Enumeration<String> names =  session.getAttributeNames() ;
        while (names.hasMoreElements()) {
            String name = names.nextElement() ;
            if (name != " " && !name.isEmpty()) {
               parametters.put(name , session.getAttribute(name) ) ;
            }
        }
    }

    public Object getParametter (String name) {
        Object rez = null ;
        rez = this.parametters.get(name) ;

        if (rez == null) {
            System.out.println("Attribut " + name + " inexistante");
        }
       return  rez ;
       
    }
    
    public void setParametter (String name , Object o ){       
        parametters.put(name, o) ;
    }
}