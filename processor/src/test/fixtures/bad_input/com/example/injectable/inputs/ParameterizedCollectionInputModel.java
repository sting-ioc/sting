package com.example.injectable.inputs;

import java.util.Collection;
import java.util.List;
import sting.Injectable;

@Injectable
public class ParameterizedCollectionInputModel
{
  ParameterizedCollectionInputModel( Collection<List<String>> someValue )
  {
  }
}
