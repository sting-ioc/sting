package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.AfterException;
import sting.interceptors.Arguments;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.MethodName;
import sting.interceptors.ServiceType;
import sting.interceptors.Thrown;

public final class LifecycleMetadataModel {
    private LifecycleMetadataModel() {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @Trace
    interface Service {
        void run(String value);
    }

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {
        public void run(final String value) {}
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before(
                @ServiceType final String serviceType,
                @MethodName final String methodName,
                @Arguments final Object[] arguments) {}

        @After
        public void after(
                @ServiceType final String serviceType,
                @MethodName final String methodName,
                @Arguments final Object[] arguments) {}

        @AfterException
        public void afterException(
                @ServiceType final String serviceType,
                @MethodName final String methodName,
                @Arguments final Object[] arguments,
                @Thrown final Throwable throwable) {}
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.LifecycleMetadataModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
