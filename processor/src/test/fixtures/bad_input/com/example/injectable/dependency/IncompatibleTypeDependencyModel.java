package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class IncompatibleTypeDependencyModel
{
  IncompatibleTypeDependencyModel( @Dependency( type = Runnable.class ) String someValue )
  {
  }
}
