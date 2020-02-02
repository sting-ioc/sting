package com.example.injectable.inputs;

import java.util.Collection;
import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalCollectionInputModel
{
  ExplicitOptionalCollectionInputModel( @Dependency( necessity = NecessityType.OPTIONAL ) Collection<String> someValue )
  {
  }
}
