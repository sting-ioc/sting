package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.type.DeclaredType;

final class IncludeDescriptor
{
  /**
   * The type that part of the include array.
   */
  @Nonnull
  private final DeclaredType _includedType;
  /**
   * The name of the actual type added to the component graph.
   * This may be a different name to the _includedType in the presence of @StingProvider annotation
   */
  @Nonnull
  private final String _actualTypeName;

  IncludeDescriptor( @Nonnull final DeclaredType includedType, @Nonnull final String actualTypeName )
  {
    _includedType = Objects.requireNonNull( includedType );
    _actualTypeName = Objects.requireNonNull( actualTypeName );
  }

  @Nonnull
  DeclaredType getIncludedType()
  {
    return _includedType;
  }

  @Nonnull
  String getActualTypeName()
  {
    return _actualTypeName;
  }

  boolean isProvider()
  {
    return !_includedType.toString().equals( _actualTypeName );
  }
}
