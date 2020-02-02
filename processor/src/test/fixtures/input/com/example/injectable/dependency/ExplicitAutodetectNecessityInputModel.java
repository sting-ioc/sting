package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitAutodetectNecessityInputModel
{
  ExplicitAutodetectNecessityInputModel( @Dependency( necessity = NecessityType.AUTODETECT ) Runnable runnable )
  {
  }
}
