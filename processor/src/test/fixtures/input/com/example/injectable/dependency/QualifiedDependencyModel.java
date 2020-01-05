package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class QualifiedDependencyModel
{
  QualifiedDependencyModel( @Dependency( qualifier = "lively" ) Runnable runnable )
  {
  }
}
