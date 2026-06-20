package com.example.interceptor;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_ResultAndThrownModel_Model_Service_InterceptorProxy
    implements ResultAndThrownModel.Service {
  @Nonnull
  private final ResultAndThrownModel.Service _target;

  @Nonnull
  private final ResultAndThrownModel.TraceInterceptor _interceptor1;

  private Sting_com_example_interceptor_ResultAndThrownModel_Model_Service_InterceptorProxy(
      final ResultAndThrownModel.Service target,
      final ResultAndThrownModel.TraceInterceptor interceptor1) {
    _target = target;
    _interceptor1 = interceptor1;
  }

  @Nonnull
  public static Object create(final Object target, final Object interceptor1) {
    return new Sting_com_example_interceptor_ResultAndThrownModel_Model_Service_InterceptorProxy(
        (ResultAndThrownModel.Service) target,
        (ResultAndThrownModel.TraceInterceptor) interceptor1);
  }

  @Override
  public String reference() {
    String result;
    try {
      result = _target.reference();
    } catch (RuntimeException t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      _interceptor1.afterException(t);
      throw t;
    }
    _interceptor1.after(result);
    return result;
  }

  @Override
  public int primitive() {
    int result;
    try {
      result = _target.primitive();
    } catch (RuntimeException t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      _interceptor1.afterException(t);
      throw t;
    }
    _interceptor1.after(result);
    return result;
  }

  @Override
  public void none() {
    try {
      _target.none();
    } catch (RuntimeException t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      _interceptor1.afterException(t);
      throw t;
    }
    _interceptor1.after(null);
  }

  @Override
  public void checked() throws IOException {
    try {
      _target.checked();
    } catch (RuntimeException t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (IOException t) {
      _interceptor1.afterException(t);
      throw t;
    }
    _interceptor1.after(null);
  }

  @Override
  public void runtime() {
    try {
      _target.runtime();
    } catch (RuntimeException t) {
      _interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      _interceptor1.afterException(t);
      throw t;
    }
    _interceptor1.after(null);
  }
}
