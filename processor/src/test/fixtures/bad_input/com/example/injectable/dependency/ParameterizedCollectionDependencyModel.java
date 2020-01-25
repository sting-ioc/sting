package com.example.injectable.dependency;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class ParameterizedCollectionDependencyModel
{
  ParameterizedCollectionDependencyModel( Collection<List<String>> someValue )
  {
  }
}
