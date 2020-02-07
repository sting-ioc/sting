package com.example.injectable.dependency;

import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitRequiredInputModel
{
  ExplicitRequiredInputModel( @Service( necessity = NecessityType.REQUIRED ) Runnable runnable )
  {
  }
}
