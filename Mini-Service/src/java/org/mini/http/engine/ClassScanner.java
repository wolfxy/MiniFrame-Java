package org.mini.http.engine;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassScanner {
    
    public static List<Class> scanAllClass(String packageName) throws Exception
    {
        String splashPackageName =  packageName.replaceAll("\\.", "/");
        ClassLoader loader = org.mini.http.engine.ClassScanner.class.getClassLoader();
        URL url = loader.getResource(splashPackageName);
        String filePath = getRootPath(url);
        List<Class> classes = new ArrayList<Class>();;
        if (filePath.endsWith(".jar")) {
            List<String> list = readFromJarFile(filePath, splashPackageName);
            if (list != null && list.size() > 0) {
                for (String f : list) {
                    int $index = f.indexOf("$");
                    if ($index != -1) {
                        f = f.substring(0, $index);
                    }
                    f = f.replaceAll("/", "\\.");
                    classes.add(Class.forName(f));
                }
            }
        }
        else {
            List<String> list  = readFromDirectory(filePath);
            if (list != null && list.size() > 0) {
                for (String f : list) {
                    int $index = f.indexOf("$");
                    if ($index != -1) {
                        f = f.substring(0, $index);
                    }
                    String className = (splashPackageName + f.substring(filePath.length())).replaceAll("/", "\\.");
                    classes.add(Class.forName(className));
                }
            }
        }
        //获取全部的类
        return classes;
    }

    public static List<String> readFromJarFile(String jarFilePath, String packageName) throws Exception
    {
        List<String> list = new ArrayList<String>();
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
        JarEntry entry = jarIn.getNextJarEntry();
        while (entry != null) {
            String name = entry.getName();
            if (name.startsWith(packageName) && name.endsWith(".class")) {
                list.add(name.substring(0, name.length() - ".class".length()));
            }
            entry = jarIn.getNextJarEntry();
        }
        return list;
    }

    private static List<String> readFromDirectory(String path) {
        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f : files) {
                if (f.isDirectory()) {
                    list.addAll(readFromDirectory(f.getAbsolutePath()));
                }
                else {
                    if (f.getName().endsWith(".class")) {
                        String fullPath = f.getAbsolutePath();
                        list.add(fullPath.substring(0,fullPath.length() - ".class".length()));
                    }
                }
            }
        }
        return list;
    }

    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');
        if (-1 == pos) {
            return fileUrl;
        }
        return fileUrl.substring(5, pos);
    }
}
