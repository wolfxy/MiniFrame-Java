package org.mini.http.server.java;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Map;

public class HttpRequest implements org.mini.http.engine.HttpRequest {

    private static final Logger logger = Logger.getLogger(org.mini.http.server.java.HttpRequest.class);

    InputStream inputStream;
    int contentLength;

    private HttpExchange httpExchange;

    private String path;

    private Map<String,String> parameters;
    private Map<String,String> headers;

    private Map<String, String> postParameters;

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getHeader(String key) {

        return headers == null? null : headers.get(key);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getPostParameter(String name) {
        if (postParameters == null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                final StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String postString = stringBuilder.toString();
                logger.info("Post string:" + postString);
                postParameters = HttpQueryParser.queryStringToMap(postString);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception e) {
                        
                    }
                }
            }
        }
        return postParameters.get(name);
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setExchange(HttpExchange httpExchange)
    {
        this.httpExchange = httpExchange;
    }

    public String getRemoteAddress()
    {
        if (this.httpExchange != null) {
            java.net.InetSocketAddress address = this.httpExchange.getRemoteAddress();
            logger.info("RemoteAddress: " + address.toString());
            InetAddress inetAddress = address.getAddress();
            if (inetAddress != null) {
                return inetAddress.getHostAddress();
            }
            return address.getHostString();
        }
        return null;
    }
}
