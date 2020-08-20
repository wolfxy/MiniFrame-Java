package org.mini.http.engine;

import org.apache.log4j.Logger;
import org.mini.http.annotation.Register;
import org.mini.http.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpEngine {
    private static final Logger LOGGER  = Logger.getLogger(org.mini.http.engine.HttpEngine.class);
    public static Map<String, HttpAction> controllerMap = new HashMap();

    public static void run(Class mainClass, int port) throws Exception {
        registerController(mainClass);
        org.mini.http.server.java.HttpServer serverN = new org.mini.http.server.java.HttpServer();
        serverN.startHttpService(port);
    }

    public static HttpAction action(String uri)
    {
        HttpAction action = controllerMap.get(uri);
        if (action == null) {
             for(String key : controllerMap.keySet()) {
                 if (uri.startsWith(key)) {
                     action = controllerMap.get(key);
                     break;
                 }
             }
        }
        return action;
    }

    public static void registerController(Class mainClass) throws Exception
    {
        Annotation annotation =  mainClass.getAnnotation(Register.class);
        String _package = ((Register) annotation).value();
        List<Class> allClasses = ClassScanner.scanAllClass(_package);
        for(Class c : allClasses)
        {
            RequestMapping classRequestMapping = (RequestMapping)c.getAnnotation(RequestMapping.class);

            if (classRequestMapping != null) {
                String classMapValue = classRequestMapping.value();
                if (classMapValue != null) {
                    if (classMapValue.length() == 0) {
                        classMapValue = "/";
                    }
                    if (!classMapValue.endsWith("/")) {
                        classMapValue = classMapValue + "/";
                    }
                    if (!classMapValue.startsWith("/")) {
                        classMapValue =  "/" + classMapValue;
                    }
                }

                Method[] methods = c.getDeclaredMethods();
                if (methods != null && methods.length > 0) {
                    for(Method m :  methods) {
                        RequestMapping methodRequestMapping = m.getAnnotation(RequestMapping.class);
                        if (methodRequestMapping != null) {
                            String mMapValue = methodRequestMapping.value();
                            if (mMapValue != null && mMapValue.length() > 0) {
                                if (mMapValue.startsWith("/")) {
                                    mMapValue = mMapValue.substring(1);
                                }
                                controllerMap.put(classMapValue + mMapValue, new HttpAction(c, m));
                            }
                        }
                    }
                }
            }
        }
        LOGGER.info("controller mapping");
        for (String path : controllerMap.keySet()) {
            HttpAction action = controllerMap.get(path);
            LOGGER.info(path + " : class is " + action.clazz.getSimpleName() + ", method is " + action.method.getName());
        }
    }
}
