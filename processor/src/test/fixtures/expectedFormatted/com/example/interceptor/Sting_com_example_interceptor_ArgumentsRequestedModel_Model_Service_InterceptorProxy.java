package com.example.interceptor;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final
class Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy
    implements ArgumentsRequestedModel.Service {
  private final ArgumentsRequestedModel.Service _target;

  private final ArgumentsRequestedModel.TraceInterceptor _interceptor1;

  private Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy(
      final ArgumentsRequestedModel.Service target,
      final ArgumentsRequestedModel.TraceInterceptor interceptor1) {
    _target = target;
    _interceptor1 = interceptor1;
  }

  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_ArgumentsRequestedModel_Model_Service_InterceptorProxy(
        (ArgumentsRequestedModel.Service) target,
        (ArgumentsRequestedModel.TraceInterceptor) interceptor1);
  }

  @Override
  public void run(final String value) {
    @Nullable Object[] arguments = null;
    if (null == arguments) {
      arguments = new Object[] {value};
    }
    _interceptor1.before(arguments);
    _target.run(value);
  }
}
