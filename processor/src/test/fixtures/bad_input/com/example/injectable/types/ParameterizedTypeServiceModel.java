package com.example.injectable.types;

import java.util.ArrayList;
import java.util.Collection;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( Collection.class )
public class ParameterizedTypeServiceModel
  extends ArrayList<String>
{
  ParameterizedTypeServiceModel()
  {
  }
}
