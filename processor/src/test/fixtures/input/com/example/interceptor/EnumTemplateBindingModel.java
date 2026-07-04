package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;

public final class EnumTemplateBindingModel {
    private EnumTemplateBindingModel() {}

    enum Mode {
        DEFAULT,
        OTHER,
        REQUIRES_NEW,
        UNUSED_MISSING
    }

    enum ThirdPartyMode {
        THIRD_PARTY
    }

    @Injector(
            includes = {DefaultModel.class, OtherModel.class, RequiresNewModel.class, ThirdPartyModel.class},
            fragmentOnly = false)
    interface MyInjector {
        DefaultService defaultService();

        OtherService otherService();

        RequiresNewService requiresNewService();

        ThirdPartyService thirdPartyService();
    }

    @Trace
    interface DefaultService {
        void run();
    }

    @Trace(Mode.OTHER)
    interface OtherService {
        void run();
    }

    @Trace(Mode.REQUIRES_NEW)
    interface RequiresNewService {
        void run();
    }

    @ThirdPartyTrace
    interface ThirdPartyService {
        void run();
    }

    @Injectable
    @Typed(DefaultService.class)
    static class DefaultModel implements DefaultService {
        public void run() {}
    }

    @Injectable
    @Typed(OtherService.class)
    static class OtherModel implements OtherService {
        public void run() {}
    }

    @Injectable
    @Typed(RequiresNewService.class)
    static class RequiresNewModel implements RequiresNewService {
        public void run() {}
    }

    @Injectable
    @Typed(ThirdPartyService.class)
    static class ThirdPartyModel implements ThirdPartyService {
        public void run() {}
    }

    @Injectable
    public static class DefaultTraceInterceptor {
        @Before
        public void before() {}
    }

    @Injectable
    public static class OtherTraceInterceptor {
        @Before
        public void before() {}
    }

    @Injectable
    public static class RequiresNewTraceInterceptor {
        @Before
        public void before() {}
    }

    @Injectable
    public static class ThirdPartyTraceInterceptor {
        @Before
        public void before() {}
    }

    @sting.interceptors.InterceptorBinding(
            implementedBy = "com.example.interceptor.EnumTemplateBindingModel.{value}TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {
        Mode value() default Mode.DEFAULT;
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.EnumTemplateBindingModel.{value}TraceInterceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface ThirdPartyTrace {
        ThirdPartyMode value() default ThirdPartyMode.THIRD_PARTY;
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface InterceptorBinding {
        String implementedBy() default "";

        int priority();
    }
}
