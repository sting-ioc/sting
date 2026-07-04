package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class QualifiedBindingModel {
    private QualifiedBindingModel() {}

    @Injector(
            includes = {LeftModel.class, RightModel.class},
            fragmentOnly = false)
    interface MyInjector {
        @Named("left")
        Service left();

        @Named("right")
        Service right();
    }

    interface Service {
        String name();
    }

    @Trace
    @Named("left")
    @Injectable
    @Typed(Service.class)
    static class LeftModel implements Service {
        public String name() {
            return "left";
        }
    }

    @Trace
    @Named("right")
    @Injectable
    @Typed(Service.class)
    static class RightModel implements Service {
        public String name() {
            return "right";
        }
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.QualifiedBindingModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
