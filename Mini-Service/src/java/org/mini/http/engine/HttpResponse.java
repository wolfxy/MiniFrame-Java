package org.mini.http.engine;

public interface HttpResponse {

     Object getData();

     void setData(Object data);
    

     void sendRedirect(String url);

     void redirect(String url);

     String getLocation();

     void setLocation(String location);
}
