package sting.processor;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

record FactoryMethodDescriptor(
        @Nonnull ExecutableElement method,
        @Nonnull TypeMirror producedType,
        @Nonnull ExecutableElement constructor,
        @Nonnull List<? extends VariableElement> constructorParameters,
        @Nonnull Map<Integer, VariableElement> methodParametersByConstructorIndex,
        @Nonnull Map<Integer, FactoryDependencyDescriptor> dependenciesByConstructorIndex) {}
