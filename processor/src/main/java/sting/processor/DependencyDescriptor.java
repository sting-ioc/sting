package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;

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
   * The element that declares this dependency.
   * The parameter will either be a parameter on a @Provides annotated method, a parameter of
   * the constructor in an @Injectable annotated type, a parameter of the abstract method in
   * the @Factory annotated type or a ExecutableElement for a dependency on an @Injector.
   */
  @Nonnull
  private final Element _element;

  DependencyDescriptor( @Nonnull final Type type,
                        @Nonnull final Coordinate coordinate,
                        final boolean optional,
                        @Nonnull final Element element )
  {
    _type = Objects.requireNonNull( type );
    _coordinate = Objects.requireNonNull( coordinate );
    _optional = optional;
    _element = Objects.requireNonNull( element );
  }

  @Nonnull
  Type getType()
  {
    return _type;
  }

  @Nonnull
  Coordinate getCoordinate()
  {
    return _coordinate;
  }

  boolean isOptional()
  {
    return _optional;
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    final DependencyDescriptor.Type type = getType();
    if ( DependencyDescriptor.Type.INSTANCE != type )
    {
      g.write( "type", type.toString() );
    }

    _coordinate.write( g );
    if ( isOptional() )
    {
      g.write( "optional", true );
    }
    g.writeEnd();
  }

  enum Type
  {
    /// A request for an instance of the dependency type T
    INSTANCE,
    /// A request for Supplier<T> of the dependency type T
    SUPPLIER
  }
}
