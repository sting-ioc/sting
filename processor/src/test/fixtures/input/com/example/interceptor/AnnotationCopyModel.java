package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class AnnotationCopyModel {
    private AnnotationCopyModel() {}

    @Trace
    interface Service {
        @Deprecated
        @Nonnull
        String run(@Nullable String value);
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        @Deprecated
        @Nonnull
        public String run(@Nullable final String value) {
            return "";
        }
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

    @InterceptorBinding(implementedBy = "com.example.interceptor.AnnotationCopyModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
