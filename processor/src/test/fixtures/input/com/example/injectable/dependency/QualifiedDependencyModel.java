package com.example.injectable.dependency;

import sting.Injectable;
import sting.Named;

@Injectable
public class QualifiedDependencyModel
{
  QualifiedDependencyModel( @Named( "lively" ) Runnable runnable )
  {
  }
}
