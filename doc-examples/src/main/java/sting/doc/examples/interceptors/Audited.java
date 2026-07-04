package sting.doc.examples.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.interceptors.InterceptorBinding;

@InterceptorBinding(implementedBy = "sting.doc.examples.interceptors.AuditInterceptor", priority = 50)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Audited {
    String action();
}
