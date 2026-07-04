package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.InterceptorBinding;

public final class NonCanonicalTemplateClassnameModel {
    private NonCanonicalTemplateClassnameModel() {}

    enum Mode {
        BAD$name
    }

    @Trace
    interface Service {}

    @Injectable
    @Typed(Service.class)
    static class Model implements Service {}

    @Injector(includes = Model.class, fragmentOnly = false)
    interface MyInjector {
        Service service();
    }

    @InterceptorBinding(
            implementedBy = "com.example.interceptor.NonCanonicalTemplateClassnameModel.{value}Interceptor",
            priority = 100)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Trace {
        Mode value() default Mode.BAD$name;
    }
}
