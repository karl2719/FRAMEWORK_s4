package com.itu4061.utils;

import java.io.File;
import java.util.ArrayList;

public class FrameworkUtils {
    public static void scanForViewJsp(File dir , String root , ArrayList<String> result){
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanForViewJsp(file, root, result);
            } else if (file.getName().endsWith(".jsp")) {
                String classname = file.getAbsolutePath()
                        .replace(root, "") ;
                result.add(classname);
            }
        }
    }
}
