package sting.processor;

import javax.annotation.Nonnull;

final class UnresolvedDeclaredTypeException
  extends RuntimeException
{
  UnresolvedDeclaredTypeException( @Nonnull final String message )
  {
    super( message );
  }
}
