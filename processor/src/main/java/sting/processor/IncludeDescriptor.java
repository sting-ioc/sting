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
  /**
   * Is the include an auto-include that is the result of being enclosed within an injector or is it an explicit include.
   */
  private final boolean _auto;

  IncludeDescriptor( @Nonnull final DeclaredType includedType, @Nonnull final String actualTypeName, final boolean auto )
  {
    _includedType = Objects.requireNonNull( includedType );
    _actualTypeName = Objects.requireNonNull( actualTypeName );
    _auto = auto;
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

  boolean isAuto()
  {
    return _auto;
  }

  boolean isProvider()
  {
    return !_includedType.toString().equals( _actualTypeName );
  }
}
