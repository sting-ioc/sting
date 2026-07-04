package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.InterceptorBinding;

public final class UnrelatedLifecycleNameModel {
    private UnrelatedLifecycleNameModel() {}

    @Trace
    interface Service {
        void run();
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public void run() {}
    }

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public static int ignored() {
            return 0;
        }

        @sting.interceptors.Before
        public void before() {}
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface Before {}

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.UnrelatedLifecycleNameModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
