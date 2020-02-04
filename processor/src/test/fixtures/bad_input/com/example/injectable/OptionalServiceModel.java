package com.example.injectable;

import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable( services = @Service( necessity = NecessityType.OPTIONAL ) )
public class OptionalServiceModel
{
  OptionalServiceModel()
  {
  }
}
