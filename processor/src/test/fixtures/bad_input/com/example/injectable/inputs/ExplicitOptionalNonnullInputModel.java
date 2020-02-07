package com.example.injectable.inputs;

import javax.annotation.Nonnull;
import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitOptionalNonnullInputModel
{
  ExplicitOptionalNonnullInputModel( @Service( necessity = NecessityType.OPTIONAL ) @Nonnull String someValue )
  {
  }
}
