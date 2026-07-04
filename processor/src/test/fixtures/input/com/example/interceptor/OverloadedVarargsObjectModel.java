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

public final class OverloadedVarargsObjectModel {
    private OverloadedVarargsObjectModel() {}

    @Trace
    interface Service {
        void run();

        void run(String value);

        void varargs(String... values);

        boolean equals(Object other);

        int hashCode();

        String toString();
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public void run() {}

        public void run(final String value) {}

        public void varargs(final String... values) {}
    }

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.OverloadedVarargsObjectModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
