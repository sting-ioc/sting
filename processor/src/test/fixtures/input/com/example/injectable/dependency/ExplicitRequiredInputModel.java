package com.example.injectable.dependency;

import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitRequiredInputModel
{
  ExplicitRequiredInputModel( @Service( necessity = NecessityType.REQUIRED ) Runnable runnable )
  {
  }
}
