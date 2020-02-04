package com.example.injectable.types;

import sting.Injectable;
import sting.Service;

@Injectable( services = @Service( type = Runnable.class ) )
public class BadType1Model
{
  BadType1Model()
  {
  }
}
