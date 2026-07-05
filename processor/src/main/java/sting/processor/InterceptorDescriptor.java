package sting.processor;

import java.util.Map;
import javax.lang.model.element.TypeElement;
import org.jspecify.annotations.Nullable;

record InterceptorDescriptor(
        TypeElement element, Map<InterceptorPhase, InterceptorMethodDescriptor> methods, Binding binding) {
    @Nullable
    InterceptorMethodDescriptor findMethod(final InterceptorPhase phase) {
        return methods.get(phase);
    }

    boolean requestsArguments() {
        return methods.values().stream()
                .flatMap(m -> m.parameters().stream())
                .anyMatch(p -> LifecycleParameterDescriptor.Kind.ARGUMENTS == p.kind());
    }
}
