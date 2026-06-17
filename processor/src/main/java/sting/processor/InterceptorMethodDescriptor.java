package sting.processor;

import java.util.List;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;

record InterceptorMethodDescriptor(@Nonnull InterceptorPhase phase, @Nonnull ExecutableElement method,
                                   @Nonnull List<LifecycleParameterDescriptor> parameters)
{
}
