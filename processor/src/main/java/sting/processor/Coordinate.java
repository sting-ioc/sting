package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.type.TypeMirror;

/**
 * The mechanism for identifying a dependency.
 */
final class Coordinate
{
  /**
   * An opaque string used to restrict the requests that match a binding.
   */
  @Nonnull
  private final String _qualifier;
  /**
   * The type that must be matched.
   */
  @Nonnull
  private final TypeMirror _type;

  Coordinate( @Nonnull final String qualifier, @Nonnull final TypeMirror type )
  {
    _qualifier = Objects.requireNonNull( qualifier );
    _type = Objects.requireNonNull( type );
  }

  @Nonnull
  String getQualifier()
  {
    return _qualifier;
  }

  @Nonnull
  TypeMirror getType()
  {
    return _type;
  }

  @Override
  public boolean equals( final Object o )
  {
    if ( this == o )
    {
      return true;
    }
    else if ( o instanceof Coordinate )
    {
      final Coordinate coordinate = (Coordinate) o;
      return _qualifier.equals( coordinate._qualifier ) && _type.equals( coordinate._type );
    }
    else
    {
      return false;
    }
  }

  @Override
  public int hashCode()
  {
    return Objects.hash( _qualifier, _type );
  }
}
