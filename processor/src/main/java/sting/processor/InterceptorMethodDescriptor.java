package sting.processor;

import java.util.List;
import javax.lang.model.element.ExecutableElement;

record InterceptorMethodDescriptor(
        InterceptorPhase phase, ExecutableElement method, List<LifecycleParameterDescriptor> parameters) {}
