package org.mini.http.engine;

import java.io.InputStream;
import java.util.Map;

public interface HttpRequest {

    int getContentLength();

    void setContentLength(int contentLength);

    InputStream getInputStream();

    void setInputStream(InputStream inputStream);

    String getParameter(String name);

    String getHeader(String key);

    Map<String, String> getParameters();

    void setParameters(Map<String, String> parameters);

    String getPath();

    void setPath(String path);

    Map<String, String> getHeaders();

    void setHeaders(Map<String, String> headers);

    String getPostParameter(String name);

    String getRemoteAddress();
}
