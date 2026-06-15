package sting.processor;

import javax.annotation.Nonnull;
import javax.lang.model.type.DeclaredType;

/**
 * @param includedType   The type that part of the include array.
 * @param actualTypeName The name of the actual type added to the component graph.
 *                       This may be a different name to the includedType in the presence of @StingProvider annotation
 * @param auto           Is the include an auto-include that is the result of being enclosed within an injector or is it an explicit include.
 */
record IncludeDescriptor(@Nonnull DeclaredType includedType, @Nonnull String actualTypeName, boolean auto)
{
  boolean isProvider()
  {
    return !includedType().toString().equals( actualTypeName() );
  }
}
