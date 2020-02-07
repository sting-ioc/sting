package com.example.injectable.dependency;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitAutodetectNecessityNullableInputModel
{
  ExplicitAutodetectNecessityNullableInputModel( @Service( necessity = NecessityType.AUTODETECT ) @Nullable Runnable runnable )
  {
  }
}
