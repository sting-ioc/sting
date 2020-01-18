package com.example.injectable.dependency;

import java.util.Collection;
import sting.Injectable;

@Injectable
public class RawCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  RawCollectionDependencyModel( Collection someValue )
  {
  }
}
