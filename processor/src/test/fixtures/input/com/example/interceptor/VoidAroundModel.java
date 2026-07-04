package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Around;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Invocation;
import sting.interceptors.Proceed;

public final class VoidAroundModel {
    private VoidAroundModel() {}

    @Trace
    interface Service {
        void run(String value);
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public void run(final String value) {}
    }

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Injectable
    public static class TraceInterceptor {
        @Around
        public Object around(@Proceed final Invocation invocation) throws Throwable {
            return invocation.proceed();
        }
    }

    @InterceptorBinding(implementedBy = "com.example.interceptor.VoidAroundModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
