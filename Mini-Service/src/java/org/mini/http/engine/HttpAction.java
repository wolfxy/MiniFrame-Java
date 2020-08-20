package org.mini.http.engine;

import java.lang.reflect.Method;

public class HttpAction {
    Class clazz;
    Method method;

    public HttpAction(Class clazz, Method method)
    {
        this.clazz = clazz;
        this.method = method;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
