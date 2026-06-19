package sting.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

record BindingValueModel(@Nonnull String name, @Nonnull BindingValueKind kind, @Nullable Object scalarValue,
                         @Nullable String className, @Nullable String enumTypeName,
                         @Nullable String enumConstantName, @Nonnull String javaLiteral)
{
  @Nonnull
  public String javaLiteral()
  {
    if ( BindingValueKind.UNSUPPORTED == kind )
    {
      throw new IllegalStateException( "Unsupported binding value " + name + " has no Java literal" );
    }
    return javaLiteral;
  }
}
