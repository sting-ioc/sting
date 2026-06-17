package sting.processor;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;

record InterceptorDescriptor(@Nonnull TypeElement element,
                             @Nonnull Map<InterceptorPhase, InterceptorMethodDescriptor> methods,
                             @Nonnull Binding binding)
{
  @Nullable
  InterceptorMethodDescriptor findMethod( @Nonnull final InterceptorPhase phase )
  {
    return methods.get( phase );
  }

  boolean requestsArguments()
  {
    return
      methods
        .values()
        .stream()
        .flatMap( m -> m.parameters().stream() )
        .anyMatch( p -> LifecycleParameterDescriptor.Kind.ARGUMENTS == p.kind() );
  }
}
