package com.example.injectable.dependency;

import javax.annotation.Nullable;
import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitAutodetectNecessityNullableInputModel
{
  ExplicitAutodetectNecessityNullableInputModel( @Service( necessity = NecessityType.AUTODETECT ) @Nullable Runnable runnable )
  {
  }
}
