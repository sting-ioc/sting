package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class ProviderBindingSourceModel {
    private ProviderBindingSourceModel() {}

    @Injector(includes = ProviderBindingSourceModel.MyFragment.class)
    interface MyInjector {
        Service service();
    }

    interface Service {
        void run();
    }

    static final class Model implements Service {
        public void run() {}
    }

    @Fragment
    interface MyFragment {
        @Trace
        default Service service() {
            return new Model();
        }
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.ProviderBindingSourceModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
