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
   * The underlying type added to the component graph.
   * This may differ from the _includedType in the presence of @StingProvider annotation
   */
  @Nonnull
  private final DeclaredType _actualType;

  IncludeDescriptor( @Nonnull final DeclaredType includedType, @Nonnull final DeclaredType actualType )
  {
    _includedType = Objects.requireNonNull( includedType );
    _actualType = Objects.requireNonNull( actualType );
  }

  @Nonnull
  DeclaredType getIncludedType()
  {
    return _includedType;
  }

  @Nonnull
  DeclaredType getActualType()
  {
    return _actualType;
  }
}
