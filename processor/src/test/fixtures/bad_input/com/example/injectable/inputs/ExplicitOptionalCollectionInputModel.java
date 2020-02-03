package com.example.injectable.inputs;

import java.util.Collection;
import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalCollectionInputModel
{
  ExplicitOptionalCollectionInputModel( @Service( necessity = NecessityType.OPTIONAL ) Collection<String> someValue )
  {
  }
}
