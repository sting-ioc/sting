package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Result;

public final class ResultWrongTypeModel {
    private ResultWrongTypeModel() {}

    @Trace
    interface Service {}

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Injectable
    public static class TraceInterceptor {
        @After
        public void after(@Result final String result) {}
    }

    @InterceptorBinding(implementedBy = "com.example.interceptor.ResultWrongTypeModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
