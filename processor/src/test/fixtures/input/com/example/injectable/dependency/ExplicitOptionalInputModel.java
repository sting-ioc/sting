package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalInputModel
{
  ExplicitOptionalInputModel( @Dependency( necessity = NecessityType.OPTIONAL ) Runnable runnable )
  {
  }
}
