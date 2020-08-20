package org.mini.http.server.java;

import com.sun.net.httpserver.HttpExchange;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpQueryParser {

    public static Map<String, String> queryToMap(HttpExchange httpExchange){
        String query = httpExchange.getRequestURI().getRawQuery();
        return  queryStringToMap(query);
    }

    public static Map<String, String> queryStringToMap(String query){
        String encoding = "UTF-8";
        Map<String, String> result = new HashMap<String, String>();
        if (query == null || query.length() == 0) {
            return result;
        }
        String[] kv = query.split("&");
        for (String param : kv) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                try {
                    result.put(pair[0], URLDecoder.decode(pair[1], encoding));
                }
                catch (Exception e) {
                }
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
