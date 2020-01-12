package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class InjectorDescriptor
{
  /**
   * The element that defined the Injector.
   * It may be either an interface or an abstract class.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * The list of types included by Injector.
   */
  @Nonnull
  private final Collection<DeclaredType> _includes;
  /**
   * The collection of dependencies declared by the injector intended to be used by Injector clients.
   */
  @Nonnull
  private final List<DependencyDescriptor> _topLevelDependencies;

  InjectorDescriptor( @Nonnull final TypeElement element,
                      @Nonnull final Collection<DeclaredType> includes,
                      @Nonnull final List<DependencyDescriptor> topLevelDependencies )
  {
    _element = Objects.requireNonNull( element );
    _includes = Objects.requireNonNull( includes );
    _topLevelDependencies = Objects.requireNonNull( topLevelDependencies );
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
  List<DependencyDescriptor> getTopLevelDependencies()
  {
    return _topLevelDependencies;
  }
}