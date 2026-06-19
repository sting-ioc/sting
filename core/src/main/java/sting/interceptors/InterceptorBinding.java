package sting.interceptors;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Marks an annotation type as an interceptor binding.
 *
 * <p>Interceptor bindings are applied to service interfaces, {@code @Injectable} implementation classes, or
 * {@code @Fragment} provider methods. Sting resolves them at compile time and generates direct service-interface
 * proxies.</p>
 */
@Documented
@Retention( RetentionPolicy.CLASS )
@Target( ElementType.ANNOTATION_TYPE )
public @interface InterceptorBinding
{
  /**
   * The canonical dotted name of the {@code @Injectable} interceptor implementation.
   *
   * @return the canonical dotted name of the interceptor implementation.
   */
  @Nonnull
  String implementedBy();

  /**
   * The interceptor priority.
   *
   * <p>Lower priorities run outermost. Equal effective priorities for one intercepted service are compile errors.</p>
   *
   * @return the interceptor priority.
   */
  int priority();
}
