package com.example.interceptor;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy implements NoArgumentsModel.Service {
  @Nonnull
  private final NoArgumentsModel.Service _target;

  @Nonnull
  private final NoArgumentsModel.TraceInterceptor _interceptor1;

  private Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy(
      final NoArgumentsModel.Service target, final NoArgumentsModel.TraceInterceptor interceptor1) {
    _target = target;
    _interceptor1 = interceptor1;
  }

  @Nonnull
  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy( (NoArgumentsModel.Service) target, (NoArgumentsModel.TraceInterceptor) interceptor1 );
  }

  @Override
  public void run(final String value) {
    _interceptor1.before();
    _target.run(value);
  }
}
