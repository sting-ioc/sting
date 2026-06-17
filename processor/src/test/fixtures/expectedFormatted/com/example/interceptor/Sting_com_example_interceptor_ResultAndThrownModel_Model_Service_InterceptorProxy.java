package com.example.interceptor;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_com_example_interceptor_ResultAndThrownModel_Model_Service_InterceptorProxy
    implements ResultAndThrownModel.Service {
  @Nonnull
  private final ResultAndThrownModel.Service $sting$_target;

  @Nonnull
  private final ResultAndThrownModel.TraceInterceptor $sting$_interceptor1;

  private Sting_com_example_interceptor_ResultAndThrownModel_Model_Service_InterceptorProxy(
      final ResultAndThrownModel.Service target,
      final ResultAndThrownModel.TraceInterceptor interceptor1) {
    $sting$_target = target;
    $sting$_interceptor1 = interceptor1;
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
      result = $sting$_target.reference();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after(result);
    return result;
  }

  @Override
  public int primitive() {
    int result;
    try {
      result = $sting$_target.primitive();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after(result);
    return result;
  }

  @Override
  public void none() {
    try {
      $sting$_target.none();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after(null);
  }

  @Override
  public void checked() throws IOException {
    try {
      $sting$_target.checked();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (IOException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after(null);
  }

  @Override
  public void runtime() {
    try {
      $sting$_target.runtime();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after(null);
  }
}
