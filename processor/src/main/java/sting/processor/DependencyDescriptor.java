package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

final class DependencyDescriptor
{
  /**
   * The type of the request.
   */
  @Nonnull
  private final Type _type;
  /**
   * The coordinate of the dependency to match.
   */
  @Nonnull
  private final Coordinate _coordinate;
  /**
   * Is the dependency optional.
   */
  private final boolean _optional;
  /**
   * The parameter that declares this dependency.
   * The parameter will either be a parameter on a @Provides annotated method, a parameter of
   * the constructor in an @Injectable annotated type or a parameter of the abstract method in
   * the @Factory annotated type.
   */
  @Nonnull
  private final VariableElement _requestElement;

  DependencyDescriptor( @Nonnull final Type type,
                        @Nonnull final Coordinate coordinate,
                        final boolean optional,
                        @Nonnull final VariableElement requestElement )
  {
    _type = Objects.requireNonNull( type );
    _coordinate = Objects.requireNonNull( coordinate );
    _optional = optional;
    _requestElement = Objects.requireNonNull( requestElement );
  }

  @Nonnull
  public Type getType()
  {
    return _type;
  }

  @Nonnull
  public Coordinate getCoordinate()
  {
    return _coordinate;
  }

  public boolean isOptional()
  {
    return _optional;
  }

  @Nonnull
  public Element getRequestElement()
  {
    return _requestElement;
  }

  enum Type
  {
    /// A request for an instance of the dependency type T
    INSTANCE,
    /// A request for Supplier<T> of the dependency type T
    SUPPLIER
  }
}
