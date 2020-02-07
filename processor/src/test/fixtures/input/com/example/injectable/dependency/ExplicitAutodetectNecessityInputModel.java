package com.example.injectable.dependency;

import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitAutodetectNecessityInputModel
{
  ExplicitAutodetectNecessityInputModel( @Service( necessity = NecessityType.AUTODETECT ) Runnable runnable )
  {
  }
}
