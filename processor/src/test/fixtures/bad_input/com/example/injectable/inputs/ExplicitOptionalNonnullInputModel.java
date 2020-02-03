package com.example.injectable.inputs;

import javax.annotation.Nonnull;
import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalNonnullInputModel
{
  ExplicitOptionalNonnullInputModel( @Service( necessity = NecessityType.OPTIONAL ) @Nonnull String someValue )
  {
  }
}
