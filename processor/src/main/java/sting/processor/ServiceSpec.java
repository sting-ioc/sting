package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;

final class ServiceSpec
{
  /**
   * The coordinate of the service to match.
   */
  @Nonnull
  private final Coordinate _coordinate;
  /**
   * Is the service optional.
   */
  private final boolean _optional;

  ServiceSpec( @Nonnull final Coordinate coordinate, final boolean optional )
  {
    _coordinate = Objects.requireNonNull( coordinate );
    _optional = optional;
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

  boolean isRequired()
  {
    return !isOptional();
  }

  boolean isPublic()
  {
    final TypeMirror type = _coordinate.getType();
    return
      TypeKind.DECLARED != type.getKind() ||
      ElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) type ).asElement() );
  }

  void write( @Nonnull final JsonGenerator g )
  {
    _coordinate.write( g );
    if ( _optional )
    {
      g.write( "optional", true );
    }
  }

  @Override
  public String toString()
  {
    return _coordinate.toString() + ( _optional ? "?" : "" );
  }
}
