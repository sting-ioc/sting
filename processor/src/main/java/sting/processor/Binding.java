package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

final class Binding
{
  /**
   * The type of the binding.
   */
  @Nonnull
  private final Type _bindingType;
  /**
   * An opaque string used to restrict the requests that match this binding.
   */
  @Nonnull
  private final String _qualifier;
  /**
   * The types that are exposed via this binding.
   */
  @Nonnull
  private final TypeMirror[] _types;
  /**
   * Is the binding eager or lazy. Eager bindings are instantiated after the injector is instantiated
   * and before it is made accessible to user-code.
   */
  private final boolean _eager;
  /**
   * The element that created this binding.
   * The field will be a {@link javax.lang.model.element.TypeElement} for an {@link Type#INJECTABLE} binding
   * otherwise it will be an {@link javax.lang.model.element.ExecutableElement} for a {@link Type#PROVIDES} binding
   * or a a {@link Type#NULLABLE_PROVIDES} binding.
   */
  @Nonnull
  private final Element _element;
  /**
   * The dependencies that need to be supplied when creating the value.
   */
  @Nonnull
  private final DependencyRequest[] _dependencies;

  Binding( @Nonnull final Type bindingType,
           @Nonnull final String qualifier,
           @Nonnull final TypeMirror[] types,
           final boolean eager,
           @Nonnull final Element element,
           @Nonnull final DependencyRequest[] dependencies )
  {
    _bindingType = Objects.requireNonNull( bindingType );
    _qualifier = Objects.requireNonNull( qualifier );
    _types = Objects.requireNonNull( types );
    _eager = eager;
    _element = Objects.requireNonNull( element );
    _dependencies = Objects.requireNonNull( dependencies );
  }

  @Nonnull
  Type getBindingType()
  {
    return _bindingType;
  }

  @Nonnull
  String getQualifier()
  {
    return _qualifier;
  }

  @Nonnull
  TypeMirror[] getTypes()
  {
    return _types;
  }

  boolean isEager()
  {
    return _eager;
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }

  @Nonnull
  DependencyRequest[] getDependencies()
  {
    return _dependencies;
  }

  enum Type
  {
    /// Instances are created by invoking the constructor
    INJECTABLE,
    /// Instances are bound by invoking @Provides annotated method
    PROVIDES,
    /// Instances are bound by invoking @Provides annotated method that is also annotated by @Nullable
    NULLABLE_PROVIDES
  }
}
