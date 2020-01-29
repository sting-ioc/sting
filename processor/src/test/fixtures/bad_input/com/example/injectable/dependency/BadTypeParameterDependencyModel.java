package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class BadTypeParameterDependencyModel
{
  BadTypeParameterDependencyModel( @Dependency( type = Runnable.class ) String someValue )
  {
  }
}
