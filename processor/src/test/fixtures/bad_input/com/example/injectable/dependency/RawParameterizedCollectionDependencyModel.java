package com.example.injectable.dependency;

import java.util.Collection;
import java.util.List;
import sting.Injectable;

@Injectable
public class RawParameterizedCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  RawParameterizedCollectionDependencyModel( Collection<List> someValue )
  {
  }
}
