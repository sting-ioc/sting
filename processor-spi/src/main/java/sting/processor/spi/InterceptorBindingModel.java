package sting.processor.spi;

import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Immutable metadata for one effective interceptor binding on one service coordinate.
 */
public interface InterceptorBindingModel
{
  /**
   * Return the fully qualified annotation type name.
   *
   * @return the fully qualified annotation type name.
   */
  @Nonnull
  String annotationTypeName();

  /**
   * Return the interceptor priority.
   *
   * @return the interceptor priority.
   */
  int priority();

  /**
   * Return the fully qualified intercepted service type name.
   *
   * @return the fully qualified intercepted service type name.
   */
  @Nonnull
  String serviceTypeName();

  /**
   * Return the qualifier key for the intercepted service coordinate.
   *
   * @return the qualifier key, or the empty string when the service is unqualified.
   */
  @Nonnull
  String qualifierKey();

  /**
   * Return the names of available binding annotation members.
   *
   * @return the names of available binding annotation members.
   */
  @Nonnull
  Set<String> valueNames();

  /**
   * Return the binding value for the specified annotation member.
   *
   * @param name the annotation member name.
   * @return the binding value for the specified annotation member.
   * @throws IllegalArgumentException if the member name is unknown.
   */
  @Nonnull
  BindingValueModel value( @Nonnull String name );
}
