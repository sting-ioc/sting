package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class ConcretePublishedTypeModel {
    private ConcretePublishedTypeModel() {}

    @Injector(includes = ConcretePublishedTypeModel.Model.class, fragmentOnly = false)
    interface MyInjector {
        Model model();
    }

    @Trace
    @Injectable
    static class Model {}

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.ConcretePublishedTypeModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
