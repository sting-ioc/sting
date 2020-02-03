package com.example.injectable.dependency;

import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitAutodetectNecessityInputModel
{
  ExplicitAutodetectNecessityInputModel( @Service( necessity = NecessityType.AUTODETECT ) Runnable runnable )
  {
  }
}
