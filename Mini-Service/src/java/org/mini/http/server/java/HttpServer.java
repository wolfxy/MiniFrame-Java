package org.mini.http.server.java;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import org.mini.http.engine.Dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class HttpServer implements HttpHandler {
    private static final Logger logger = Logger.getLogger(org.mini.http.server.java.HttpServer.class);

    public void startHttpService(int port) throws Exception {
        try {
            InetSocketAddress addr = new InetSocketAddress(port);
            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(addr, 0);
            server.createContext("/", this);
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server is listening on port " + port);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't start server at " + port);
        }
    }
    
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            URI uri = httpExchange.getRequestURI();
            logger.info("request uri : " + uri.toString());
            String path = uri.getPath();
            HttpRequest request = new HttpRequest();
            request.setPath(path);
            Headers requestHeaders = httpExchange.getRequestHeaders();
            Map map = new HashMap();
            for (String k : requestHeaders.keySet()) {
                List<String> v = requestHeaders.get(k);
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : v) {
                    stringBuilder.append(s).append("; ");
                }
                map.put(k, stringBuilder.toString());
            }
            request.setHeaders(map);
            request.setInputStream(httpExchange.getRequestBody());
            request.setParameters(HttpQueryParser.queryToMap(httpExchange));
            request.setExchange(httpExchange);
            
            HttpResponse response = new HttpResponse();
            Dispatcher.dispatch(request, response);
            
            String redirectAddress = response.getRedirectAddress();
            if (redirectAddress != null) {
                logger.info("forward address: " + redirectAddress);
                try {
                    forward(redirectAddress, httpExchange);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                String location = response.getLocation();
                if (location != null && location.length() > 0) {
                    logger.info("location:" + location);
                    Headers headers = httpExchange.getResponseHeaders();
                    headers.set("Location", location);
                    httpExchange.sendResponseHeaders(302, 0);
                    httpExchange.getResponseBody().close();
                } else {
                    responseHeaders.set("Content-Type", "application/json; charset=utf-8");
                    httpExchange.sendResponseHeaders(200, 0);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    Object data = response.getData();
                    byte[] bytes = null;
                    if (!(data instanceof String)) {
                        bytes = JSON.toJSONString(data).getBytes();
                    } else {
                        bytes = ((String) data).getBytes();
                    }
                    responseBody.write(bytes);
                    responseBody.close();
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            httpExchange.sendResponseHeaders(500, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write("Error".getBytes());
            responseBody.close();
        }
    }

    private void forward(String address, HttpExchange httpExchange) throws Exception
    {
        URL url = new URL(address);
        HttpURLConnection connection = null;
        OutputStream  outputStream = null;
        InputStream in = null;
        Headers responseHeaders = httpExchange.getResponseHeaders();
        try {
            connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();
            Map<String, List<String>> map = connection.getHeaderFields();
            Iterator<String> keys = map.keySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key == null) {
                    continue;
                }
                responseHeaders.put(key, map.get(key));
            }
            httpExchange.sendResponseHeaders(200, 0);
            int b = -1;
            outputStream = httpExchange.getResponseBody();
            while ((b = in.read()) != -1) {
                outputStream.write(b);
            }
        }
        finally {
            if (in != null) {
               try { in.close(); in = null;} catch (Exception e) {}
            }
            if (connection != null) {
                try { connection.disconnect(); connection = null;} catch (Exception e) {}
            }
            if (outputStream != null) {
                try { outputStream.close(); outputStream = null;} catch (Exception e) {}
            }
        }
    }
}
