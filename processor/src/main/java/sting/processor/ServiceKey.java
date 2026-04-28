package sting.processor;

import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * A normalized service key used for matching requests to providers.
 */
final class ServiceKey
{
  @Nonnull
  private static final Map<TypeKind, String> BOXED_TYPES =
    Map.of( TypeKind.BOOLEAN, "java.lang.Boolean",
            TypeKind.BYTE, "java.lang.Byte",
            TypeKind.CHAR, "java.lang.Character",
            TypeKind.DOUBLE, "java.lang.Double",
            TypeKind.FLOAT, "java.lang.Float",
            TypeKind.INT, "java.lang.Integer",
            TypeKind.LONG, "java.lang.Long",
            TypeKind.SHORT, "java.lang.Short" );
  @Nonnull
  private final String _qualifier;
  @Nonnull
  private final String _type;

  ServiceKey( @Nonnull final Coordinate coordinate )
  {
    _qualifier = coordinate.getQualifier();
    _type = normalizeType( coordinate.getType() );
  }

  @Nonnull
  private static String normalizeType( @Nonnull final TypeMirror type )
  {
    return BOXED_TYPES.getOrDefault( type.getKind(), type.toString() );
  }

  @Override
  public boolean equals( final Object o )
  {
    assert o instanceof ServiceKey;
    final ServiceKey key = (ServiceKey) o;
    return matches( _qualifier, _type, key._qualifier, key._type );
  }

  private static boolean matches( @Nonnull final String lhsQualifier,
                                  @Nonnull final String lhsType,
                                  @Nonnull final String rhsQualifier,
                                  @Nonnull final String rhsType )
  {
    return lhsQualifier.equals( rhsQualifier ) && lhsType.equals( rhsType );
  }

  static boolean matches( @Nonnull final Coordinate lhsCoordinate,
                          @Nonnull final Coordinate rhsCoordinate )
  {
    return matches( lhsCoordinate.getQualifier(), normalizeType( lhsCoordinate.getType() ),
                    rhsCoordinate.getQualifier(), normalizeType( rhsCoordinate.getType() ) );
  }

  @Override
  public int hashCode()
  {
    return Objects.hash( _qualifier, _type );
  }
}
