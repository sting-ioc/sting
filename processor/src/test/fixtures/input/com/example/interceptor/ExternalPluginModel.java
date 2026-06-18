package com.example.interceptor;

import java.util.Objects;
import javax.annotation.Nonnull;
import sting.processor.spi.InterceptedMethodModel;
import sting.processor.spi.InterceptorBindingModel;
import sting.processor.spi.InterceptorCodeGenerator;
import sting.processor.spi.LifecycleCodeEmitter;

public final class ExternalPluginModel
  implements InterceptorCodeGenerator
{
  public boolean supports( @Nonnull final InterceptorBindingModel binding )
  {
    return binding.annotationTypeName().endsWith( ".PluginTrace" ) &&
           binding.priority() >= 0 &&
           !binding.serviceTypeName().isEmpty() &&
           binding.valueNames().contains( "value" );
  }

  public void emitBefore( @Nonnull final InterceptedMethodModel method,
                          @Nonnull final InterceptorBindingModel binding,
                          @Nonnull final LifecycleCodeEmitter emitter )
  {
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.serviceType() + ");" );
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.methodName() + ");" );
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.bindingValue( "value" ) + ");" );
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.argumentsArray() + ");" );
    Objects.requireNonNull( method.methodName() );
    Objects.requireNonNull( method.returnTypeName() );
    Objects.requireNonNull( method.parameterTypeNames() );
    Objects.requireNonNull( method.thrownTypeNames() );
    method.defaultMethod();
    method.varArgs();
  }

  public void emitAfter( @Nonnull final InterceptedMethodModel method,
                         @Nonnull final InterceptorBindingModel binding,
                         @Nonnull final LifecycleCodeEmitter emitter )
  {
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.result() + ");" );
  }

  public void emitAfterException( @Nonnull final InterceptedMethodModel method,
                                  @Nonnull final InterceptorBindingModel binding,
                                  @Nonnull final LifecycleCodeEmitter emitter )
  {
    emitter.emitStatement( "java.util.Objects.requireNonNull(" + emitter.thrown() + ");" );
  }
}
