package org.mini.http.engine;

import org.apache.log4j.Logger;
import org.mini.http.engine.HttpAction;
import org.mini.http.engine.HttpEngine;
import org.mini.http.engine.HttpRequest;
import org.mini.http.engine.HttpResponse;

import java.util.HashMap;
import java.util.Map;


public class Dispatcher {
    private static final Logger LOGGER  = Logger.getLogger(org.mini.http.engine.Dispatcher.class);
    public static void dispatch(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        HttpAction action = HttpEngine.action(path);
        if (action != null) {
            try {
                Object object = action.clazz.newInstance();
                action.method.invoke(object,request, response);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                Map map = new HashMap();
                map.put("code", 1);
                map.put("desc", e.getMessage());
                response.setData(map);
            }
        }
        else {
            LOGGER.info("not found action for " + path);
            Map map = new HashMap();
            map.put("code", 1);
            map.put("desc", "not found any service for " + path);
            response.setData(map);
        }
    }
}
