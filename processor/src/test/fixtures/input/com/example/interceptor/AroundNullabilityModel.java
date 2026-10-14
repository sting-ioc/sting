package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Around;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Invocation;
import sting.interceptors.Proceed;

@NullMarked
public final class AroundNullabilityModel {
    private AroundNullabilityModel() {}

    @Trace
    interface NonnullService {
        String run();
    }

    @Trace
    interface NullableService {
        @Nullable
        String run();
    }

    @Trace
    interface PrimitiveService {
        int run();
    }

    @Injectable
    @Typed(NonnullService.class)
    static class NonnullModel implements NonnullService {
        public String run() {
            return "";
        }
    }

    @Injectable
    @Typed(NullableService.class)
    static class NullableModel implements NullableService {
        @Nullable
        public String run() {
            return null;
        }
    }

    @Injectable
    @Typed(PrimitiveService.class)
    static class PrimitiveModel implements PrimitiveService {
        public int run() {
            return 0;
        }
    }

    @Injector(
            includes = {NonnullModel.class, NullableModel.class, PrimitiveModel.class},
            fragmentOnly = false)
    interface MyInjector {
        NonnullService nonnullService();

        NullableService nullableService();

        PrimitiveService primitiveService();
    }

    @Injectable
    public static class TraceInterceptor {
        @Around
        public @org.jspecify.annotations.Nullable Object around(@Proceed final Invocation invocation) throws Throwable {
            return invocation.proceed();
        }
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.AroundNullabilityModel.TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {}
}
