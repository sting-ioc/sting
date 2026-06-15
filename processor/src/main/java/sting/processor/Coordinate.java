package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.type.TypeMirror;

/**
 * The mechanism for identifying a service.
 *
 * @param qualifier An opaque string used to restrict the services that match a coordinate.
 * @param type      The java type of the service.
 */
record Coordinate(@Nonnull String qualifier, @Nonnull TypeMirror type)
{
  void write( @Nonnull final JsonGenerator g )
  {
    if ( !qualifier().isEmpty() )
    {
      g.write( "qualifier", qualifier() );
    }
    g.write( "type", type().toString() );
  }

  @Nonnull
  @Override
  public String toString()
  {
    return "[" + type() + ( qualifier().isEmpty() ? "" : ";qualifier='" + qualifier() + "'" ) + "]";
  }

  @Override
  public boolean equals( final Object o )
  {
    assert o instanceof Coordinate;
    final Coordinate coordinate = (Coordinate) o;
    return qualifier.equals( coordinate.qualifier ) && type.toString().equals( coordinate.type.toString() );
  }

  @Override
  public int hashCode()
  {
    return Objects.hash( qualifier, type.toString() );
  }
}
