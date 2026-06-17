package com.example.interceptor;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final
class Sting_com_example_interceptor_LifecycleFailureNestingModel_Model_Service_InterceptorProxy
    implements LifecycleFailureNestingModel.Service {
  @Nonnull
  private final LifecycleFailureNestingModel.Service $sting$_target;

  @Nonnull
  private final LifecycleFailureNestingModel.OuterInterceptor $sting$_interceptor1;

  @Nonnull
  private final LifecycleFailureNestingModel.InnerInterceptor $sting$_interceptor2;

  private Sting_com_example_interceptor_LifecycleFailureNestingModel_Model_Service_InterceptorProxy(
      final LifecycleFailureNestingModel.Service target,
      final LifecycleFailureNestingModel.OuterInterceptor interceptor1,
      final LifecycleFailureNestingModel.InnerInterceptor interceptor2) {
    $sting$_target = target;
    $sting$_interceptor1 = interceptor1;
    $sting$_interceptor2 = interceptor2;
  }

  @Nonnull
  public static Object create(
      final Object target, final Object interceptor1, final Object interceptor2) {
    return new Sting_com_example_interceptor_LifecycleFailureNestingModel_Model_Service_InterceptorProxy(
        (LifecycleFailureNestingModel.Service) target,
        (LifecycleFailureNestingModel.OuterInterceptor) interceptor1,
        (LifecycleFailureNestingModel.InnerInterceptor) interceptor2);
  }

  @Override
  public void run() {
    $sting$_interceptor1.before();
    try {
      $sting$_interceptor2.before();
      try {
        $sting$_target.run();
      } catch (RuntimeException t) {
        $sting$_interceptor2.afterException(t);
        throw t;
      } catch (Error t) {
        $sting$_interceptor2.afterException(t);
        throw t;
      }
      $sting$_interceptor2.after();
    } catch (RuntimeException t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    } catch (Error t) {
      $sting$_interceptor1.afterException(t);
      throw t;
    }
    $sting$_interceptor1.after();
  }
}
