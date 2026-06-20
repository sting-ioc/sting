package com.example.interceptor;

import java.lang.reflect.UndeclaredThrowableException;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import sting.interceptors.Invocation;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy implements AroundMetadataModel.Service {
  @Nonnull
  private final AroundMetadataModel.Service _target;

  @Nonnull
  private final AroundMetadataModel.TraceInterceptor _interceptor1;

  private Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy(
      final AroundMetadataModel.Service target,
      final AroundMetadataModel.TraceInterceptor interceptor1) {
    _target = target;
    _interceptor1 = interceptor1;
  }

  @Nonnull
  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy( (AroundMetadataModel.Service) target, (AroundMetadataModel.TraceInterceptor) interceptor1 );
  }

  @Override
  public String run(final String value, final int count) {
    final Object[] arguments = new Object[] {value, count};
    try {
      return (String) invoke_run_interceptor2( arguments );
    } catch ( RuntimeException t ) {
      throw t;
    } catch ( Error t ) {
      throw t;
    } catch ( Throwable t ) {
      throw new UndeclaredThrowableException( t );
    }
  }

  private Object invoke_run_interceptor2(@Nonnull final Object[] arguments) throws Throwable {
    assert null != arguments && 2 == arguments.length;
    final Object result;
    final Invocation invocation = new Invocation( nextArguments -> invoke_run_target( nextArguments ), arguments );
    result = (String) _interceptor1.around(invocation, "com.example.interceptor.AroundMetadataModel.Service", "run", "metadata", 7, arguments);
    return result;
  }

  private Object invoke_run_target(@Nonnull final Object[] arguments) throws Throwable {
    assert null != arguments && 2 == arguments.length;
    return _target.run((String) arguments[0], (int) arguments[1]);
  }
}
