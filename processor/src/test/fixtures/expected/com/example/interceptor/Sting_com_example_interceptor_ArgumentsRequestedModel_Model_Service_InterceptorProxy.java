package com.example.interceptor;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy implements ArgumentsRequestedModel.Service {
  @Nonnull
  private final ArgumentsRequestedModel.Service $sting$_target;

  @Nonnull
  private final ArgumentsRequestedModel.TraceInterceptor $sting$_interceptor1;

  private Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy(
      final ArgumentsRequestedModel.Service target,
      final ArgumentsRequestedModel.TraceInterceptor interceptor1) {
    $sting$_target = target;
    $sting$_interceptor1 = interceptor1;
  }

  @Nonnull
  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy( (ArgumentsRequestedModel.Service) target, (ArgumentsRequestedModel.TraceInterceptor) interceptor1 );
  }

  @Override
  public void run(final String value) {
    Object[] arguments = null;
    if ( null == arguments ) {
      arguments = new Object[] {value};
    }
    $sting$_interceptor1.before(arguments);
    $sting$_target.run(value);
  }
}
