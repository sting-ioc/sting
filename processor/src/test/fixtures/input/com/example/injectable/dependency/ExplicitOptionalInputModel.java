package com.example.injectable.dependency;

import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalInputModel
{
  ExplicitOptionalInputModel( @Service( necessity = NecessityType.OPTIONAL ) Runnable runnable )
  {
  }
}
