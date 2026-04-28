package sting.processor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

final class FactoryMethodDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final TypeMirror _producedType;
  @Nonnull
  private final ExecutableElement _constructor;
  @Nonnull
  private final List<? extends VariableElement> _constructorParameters;
  @Nonnull
  private final Map<Integer, VariableElement> _methodParametersByConstructorIndex;
  @Nonnull
  private final Map<Integer, FactoryDependencyDescriptor> _dependenciesByConstructorIndex;

  FactoryMethodDescriptor( @Nonnull final ExecutableElement method,
                           @Nonnull final TypeMirror producedType,
                           @Nonnull final ExecutableElement constructor,
                           @Nonnull final List<? extends VariableElement> constructorParameters,
                           @Nonnull final Map<Integer, VariableElement> methodParametersByConstructorIndex,
                           @Nonnull final Map<Integer, FactoryDependencyDescriptor> dependenciesByConstructorIndex )
  {
    _method = Objects.requireNonNull( method );
    _producedType = Objects.requireNonNull( producedType );
    _constructor = Objects.requireNonNull( constructor );
    _constructorParameters = Objects.requireNonNull( constructorParameters );
    _methodParametersByConstructorIndex = Objects.requireNonNull( methodParametersByConstructorIndex );
    _dependenciesByConstructorIndex = Objects.requireNonNull( dependenciesByConstructorIndex );
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  @Nonnull
  TypeMirror getProducedType()
  {
    return _producedType;
  }

  @Nonnull
  ExecutableElement getConstructor()
  {
    return _constructor;
  }

  @Nonnull
  List<? extends VariableElement> getConstructorParameters()
  {
    return _constructorParameters;
  }

  @Nonnull
  Map<Integer, VariableElement> getMethodParametersByConstructorIndex()
  {
    return _methodParametersByConstructorIndex;
  }

  @Nonnull
  Map<Integer, FactoryDependencyDescriptor> getDependenciesByConstructorIndex()
  {
    return _dependenciesByConstructorIndex;
  }
}
