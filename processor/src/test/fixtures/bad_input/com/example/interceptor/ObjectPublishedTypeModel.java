package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class ObjectPublishedTypeModel {
    private ObjectPublishedTypeModel() {}

    @Trace
    @Injectable
    @Typed(Object.class)
    static class Model {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Object model();
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.ObjectPublishedTypeModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
