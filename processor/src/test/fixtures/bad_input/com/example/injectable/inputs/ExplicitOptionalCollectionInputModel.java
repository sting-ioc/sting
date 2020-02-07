package com.example.injectable.inputs;

import java.util.Collection;
import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitOptionalCollectionInputModel
{
  ExplicitOptionalCollectionInputModel( @Service( necessity = NecessityType.OPTIONAL ) Collection<String> someValue )
  {
  }
}
