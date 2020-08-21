package org.mini.http.engine;

public interface HttpResponse {

     Object getData();

     void setData(Object data);
    

     void redirect(String url);

     void forward(String url);

     String getLocation();

     void setLocation(String location);
}
