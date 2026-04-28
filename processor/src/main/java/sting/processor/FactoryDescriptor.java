package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

final class FactoryDescriptor
{
  @Nonnull
  private final TypeElement _element;
  @Nonnull
  private final Collection<FactoryMethodDescriptor> _methods;
  @Nonnull
  private final Collection<FactoryDependencyDescriptor> _dependencies;
  private boolean _generated;

  FactoryDescriptor( @Nonnull final TypeElement element,
                     @Nonnull final Collection<FactoryMethodDescriptor> methods,
                     @Nonnull final Collection<FactoryDependencyDescriptor> dependencies )
  {
    assert ElementKind.INTERFACE == element.getKind();
    _element = Objects.requireNonNull( element );
    _methods = Objects.requireNonNull( methods );
    _dependencies = Objects.requireNonNull( dependencies );
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  Collection<FactoryMethodDescriptor> getMethods()
  {
    return _methods;
  }

  @Nonnull
  Collection<FactoryDependencyDescriptor> getDependencies()
  {
    return _dependencies;
  }

  boolean isGenerated()
  {
    return _generated;
  }

  void markGenerated()
  {
    _generated = true;
  }
}
