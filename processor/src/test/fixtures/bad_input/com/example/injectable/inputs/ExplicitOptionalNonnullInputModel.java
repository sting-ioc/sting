package com.example.injectable.inputs;

import javax.annotation.Nonnull;
import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalNonnullInputModel
{
  ExplicitOptionalNonnullInputModel( @Dependency( necessity = NecessityType.OPTIONAL ) @Nonnull String someValue )
  {
  }
}
