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

public final class MultiServiceBindingModel {
    private MultiServiceBindingModel() {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        ServiceA serviceA();

        ServiceB serviceB();
    }

    interface ServiceA {
        void runA();
    }

    interface ServiceB {
        void runB();
    }

    @Trace
    @Injectable
    @Typed({ServiceA.class, ServiceB.class})
    static class Model implements ServiceA, ServiceB {
        public void runA() {}

        public void runB() {}
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.MultiServiceBindingModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
