package sting.processor.spi;

import javax.annotation.Nonnull;

/**
 * Processor-path plugin extension point for direct interceptor code generation.
 */
public interface InterceptorCodeGenerator
{
  /**
   * Return true if this generator claims the effective interceptor binding.
   *
   * @param binding the effective interceptor binding.
   * @return true if this generator claims the effective interceptor binding.
   */
  boolean supports( @Nonnull InterceptorBindingModel binding );

  /**
   * Emit code that runs before the target method invocation.
   *
   * @param method the intercepted method metadata.
   * @param binding the effective interceptor binding.
   * @param emitter the lifecycle code emitter.
   */
  void emitBefore( @Nonnull InterceptedMethodModel method,
                   @Nonnull InterceptorBindingModel binding,
                   @Nonnull LifecycleCodeEmitter emitter );

  /**
   * Emit code that runs after a successful target method invocation.
   *
   * @param method the intercepted method metadata.
   * @param binding the effective interceptor binding.
   * @param emitter the lifecycle code emitter.
   */
  void emitAfter( @Nonnull InterceptedMethodModel method,
                  @Nonnull InterceptorBindingModel binding,
                  @Nonnull LifecycleCodeEmitter emitter );

  /**
   * Emit code that runs when the target method invocation or an inner interceptor fails.
   *
   * @param method the intercepted method metadata.
   * @param binding the effective interceptor binding.
   * @param emitter the lifecycle code emitter.
   */
  void emitAfterException( @Nonnull InterceptedMethodModel method,
                           @Nonnull InterceptorBindingModel binding,
                           @Nonnull LifecycleCodeEmitter emitter );
}
