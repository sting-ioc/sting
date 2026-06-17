package sting.processor;

import javax.annotation.Nonnull;

/**
 * Emits raw Java statements and exposes expressions for interceptor lifecycle metadata.
 */
public interface LifecycleCodeEmitter
{
  /**
   * Return a Java expression for the fully qualified intercepted service type name.
   *
   * @return a Java expression for the fully qualified intercepted service type name.
   */
  @Nonnull
  String serviceType();

  /**
   * Return a Java expression for the intercepted method name.
   *
   * @return a Java expression for the intercepted method name.
   */
  @Nonnull
  String methodName();

  /**
   * Return a Java expression for the specified binding annotation member value.
   *
   * @param name the binding annotation member name.
   * @return a Java expression for the specified binding annotation member value.
   */
  @Nonnull
  String bindingValue( @Nonnull String name );

  /**
   * Return a Java expression for an intercepted method argument.
   *
   * @param index the zero-based argument index.
   * @return a Java expression for an intercepted method argument.
   */
  @Nonnull
  String argument( int index );

  /**
   * Return a Java expression for the shared {@code Object[]} argument metadata.
   *
   * @return a Java expression for the shared {@code Object[]} argument metadata.
   */
  @Nonnull
  String argumentsArray();

  /**
   * Return a Java expression for the successful result value.
   *
   * @return a Java expression for the successful result value.
   * @throws RuntimeException if called outside after-phase emission.
   */
  @Nonnull
  String result();

  /**
   * Return a Java expression for the thrown failure.
   *
   * @return a Java expression for the thrown failure.
   * @throws RuntimeException if called outside after-exception-phase emission.
   */
  @Nonnull
  String thrown();

  /**
   * Emit a complete Java statement including any required semicolon.
   *
   * @param javaStatement the Java statement to emit.
   */
  void emitStatement( @Nonnull String javaStatement );
}
