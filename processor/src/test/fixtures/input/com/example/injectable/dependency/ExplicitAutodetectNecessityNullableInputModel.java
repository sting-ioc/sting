package com.example.injectable.dependency;

import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitAutodetectNecessityNullableInputModel
{
  ExplicitAutodetectNecessityNullableInputModel( @Dependency( necessity = NecessityType.AUTODETECT ) @Nullable Runnable runnable )
  {
  }
}
