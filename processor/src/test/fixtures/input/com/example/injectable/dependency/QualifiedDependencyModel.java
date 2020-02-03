package com.example.injectable.dependency;

import sting.Service;
import sting.Injectable;

@Injectable
public class QualifiedDependencyModel
{
  QualifiedDependencyModel( @Service( qualifier = "lively" ) Runnable runnable )
  {
  }
}
