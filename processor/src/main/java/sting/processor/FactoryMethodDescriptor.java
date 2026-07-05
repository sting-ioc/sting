package sting.processor;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

record FactoryMethodDescriptor(
        ExecutableElement method,
        TypeMirror producedType,
        ExecutableElement constructor,
        List<? extends VariableElement> constructorParameters,
        Map<Integer, VariableElement> methodParametersByConstructorIndex,
        Map<Integer, FactoryDependencyDescriptor> dependenciesByConstructorIndex) {}
