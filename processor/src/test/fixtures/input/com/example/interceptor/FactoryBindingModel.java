package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Factory;
import sting.Injectable;
import sting.Injector;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class FactoryBindingModel {
    private FactoryBindingModel() {}

    static final class Model {
        Model() {}
    }

    @Trace
    @Factory
    interface ModelFactory {
        Model create();
    }

    @Injector(includes = ModelFactory.class, fragmentOnly = false)
    interface MyInjector {
        ModelFactory factory();
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(implementedBy = "com.example.interceptor.FactoryBindingModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
