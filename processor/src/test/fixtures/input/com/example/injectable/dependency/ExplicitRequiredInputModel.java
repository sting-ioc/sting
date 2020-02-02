package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitRequiredInputModel
{
  ExplicitRequiredInputModel( @Dependency( necessity = NecessityType.REQUIRED ) Runnable runnable )
  {
  }
}
