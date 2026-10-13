package com.example.interceptor;

import java.lang.reflect.UndeclaredThrowableException;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.interceptors.Invocation;

@NullMarked
@Generated("sting.processor.StingProcessor")
@SuppressWarnings("Varifier")
public final class Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy implements AroundMetadataModel.Service {
  private final AroundMetadataModel.Service _target;

  private final AroundMetadataModel.TraceInterceptor _interceptor1;

  private Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy(
      final AroundMetadataModel.Service target,
      final AroundMetadataModel.TraceInterceptor interceptor1) {
    _target = target;
    _interceptor1 = interceptor1;
  }

  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_AroundMetadataModel_Model_Service_InterceptorProxy( (AroundMetadataModel.Service) target, (AroundMetadataModel.TraceInterceptor) interceptor1 );
  }

  @Override
  public String run(final String value, final int count) {
    final @Nullable Object[] arguments = new Object[] {value, count};
    try {
      return (String) $sting$_cast( invoke_run_interceptor2( arguments ) );
    } catch ( RuntimeException t ) {
      throw t;
    } catch ( Error t ) {
      throw t;
    } catch ( Throwable t ) {
      throw new UndeclaredThrowableException( t );
    }
  }

  private @Nullable Object invoke_run_interceptor2(final @Nullable Object[] arguments) throws
      Throwable {
    assert null != arguments && 2 == arguments.length;
    final Object result;
    final var invocation = new Invocation( nextArguments -> invoke_run_target( nextArguments ), arguments );
    result = (String) $sting$_cast( _interceptor1.around(invocation, "com.example.interceptor.AroundMetadataModel.Service", "run", "metadata", 7, arguments) );
    return result;
  }

  private @Nullable Object invoke_run_target(final @Nullable Object[] arguments) throws Throwable {
    assert null != arguments && 2 == arguments.length;
    return _target.run((String) $sting$_cast( arguments[0] ), (int) $sting$_cast( arguments[1] ));
  }

  @SuppressWarnings({"unchecked", "NullAway"})
  private static <T> T $sting$_cast(final @Nullable Object value) {
    return (T) value;
  }
}
