package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class FragmentDescriptor
{
  /**
   * The element declaring the fragment.
   * It must be an interface.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * The list of types included by fragment.
   */
  @Nonnull
  private final Collection<DeclaredType> _includes;
  /**
   * The bindings that the fragment explicitly declares.
   */
  @Nonnull
  private final Collection<Binding> _bindings;

  FragmentDescriptor( @Nonnull final TypeElement element,
                      @Nonnull final Collection<DeclaredType> includes,
                      @Nonnull final Collection<Binding> bindings )
  {
    assert ElementKind.INTERFACE == element.getKind();
    _element = Objects.requireNonNull( element );
    _includes = Objects.requireNonNull( includes );
    _bindings = Objects.requireNonNull( bindings );
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  Collection<DeclaredType> getIncludes()
  {
    return _includes;
  }

  @Nonnull
  Collection<Binding> getBindings()
  {
    return _bindings;
  }
}
