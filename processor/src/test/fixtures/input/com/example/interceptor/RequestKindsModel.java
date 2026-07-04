package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class RequestKindsModel {
    private RequestKindsModel() {}

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

        Supplier<Service> supplier();

        Optional<Service> optional();

        Supplier<Optional<Service>> supplierOptional();

        Collection<Service> collection();

        Collection<Supplier<Service>> supplierCollection();

        Collection<Supplier<Optional<Service>>> supplierOptionalCollection();
    }

    @Injectable
    public static class TraceInterceptor {
        @Before
        public void before() {}
    }

    @InterceptorBinding(implementedBy = "com.example.interceptor.RequestKindsModel.TraceInterceptor", priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
