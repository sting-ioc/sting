package com.example.interceptor;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.AfterException;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Result;
import sting.interceptors.Thrown;

public final class ResultAndThrownModel {
    private ResultAndThrownModel() {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Trace
    interface Service {
        String reference();

        int primitive();

        void none();

        void checked() throws IOException;

        void runtime();
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public String reference() {
            return "";
        }

        public int primitive() {
            return 0;
        }

        public void none() {}

        public void checked() throws IOException {
            throw new IOException();
        }

        public void runtime() {
            throw new IllegalStateException();
        }
    }

    @Injectable
    public static class TraceInterceptor {
        @After
        public void after(@Result final Object result) {}

        @AfterException
        public void afterException(@Thrown final Throwable throwable) {}
    }

    @InterceptorBinding(implementedBy = "com.example.interceptor.ResultAndThrownModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
