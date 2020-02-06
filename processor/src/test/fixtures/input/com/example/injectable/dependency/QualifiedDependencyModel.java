package com.example.injectable.dependency;

import sting.Named;
import sting.Service;
import sting.Injectable;

@Injectable
public class QualifiedDependencyModel
{
  QualifiedDependencyModel( @Named( "lively" ) Runnable runnable )
  {
  }
}
