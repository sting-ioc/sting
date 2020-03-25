package sting.processor;

final class UnresolvedDeclaredTypeException
  extends RuntimeException
{
   UnresolvedDeclaredTypeException( final String message )
  {
    super( message );
  }
}
