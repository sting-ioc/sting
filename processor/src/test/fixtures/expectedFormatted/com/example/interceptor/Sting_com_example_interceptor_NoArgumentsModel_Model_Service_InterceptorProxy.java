package com.example.interceptor;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy
    implements NoArgumentsModel.Service {
  @Nonnull
  private final NoArgumentsModel.Service $sting$_target;

  @Nonnull
  private final NoArgumentsModel.TraceInterceptor $sting$_interceptor1;

  private Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy(
      final NoArgumentsModel.Service target, final NoArgumentsModel.TraceInterceptor interceptor1) {
    $sting$_target = target;
    $sting$_interceptor1 = interceptor1;
  }

  @Nonnull
  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_NoArgumentsModel_Model_Service_InterceptorProxy(
        (NoArgumentsModel.Service) target, (NoArgumentsModel.TraceInterceptor) interceptor1);
  }

  @Override
  public void run(String value) {
    $sting$_interceptor1.before();
    $sting$_target.run(value);
  }
}
