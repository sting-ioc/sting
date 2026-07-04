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

public final class RedeclaredLifecycleOverrideModel {
    private RedeclaredLifecycleOverrideModel() {}

    @Trace
    interface Service {
        void run();
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public void run() {}
    }

    public static class BaseInterceptor {
        @Before
        public void before() {}
    }

    @Injectable
    public static class TraceInterceptor extends BaseInterceptor {
        @Before
        public void before() {}
    }

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.RedeclaredLifecycleOverrideModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
