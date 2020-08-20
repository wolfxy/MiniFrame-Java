package org.mini.http.server.java;

public class HttpResponse implements org.mini.http.engine.HttpResponse {

    private Object data;

    private String location = null;

    private String redirectAddress = null;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    

    public void sendRedirect(String url)
    {
        location = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void redirect(String url)
    {
        this.redirectAddress = url;
    }

    public String getRedirectAddress()
    {
        return  this.redirectAddress;
    }
}