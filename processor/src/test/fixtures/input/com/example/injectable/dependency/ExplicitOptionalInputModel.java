package com.example.injectable.dependency;

import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitOptionalInputModel
{
  ExplicitOptionalInputModel( @Service( necessity = NecessityType.OPTIONAL ) Runnable runnable )
  {
  }
}
