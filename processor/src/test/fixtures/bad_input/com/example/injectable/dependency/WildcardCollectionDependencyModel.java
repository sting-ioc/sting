package com.example.injectable.dependency;

import java.util.Collection;
import sting.Injectable;

@Injectable
public class WildcardCollectionDependencyModel
{
  WildcardCollectionDependencyModel( Collection<?> someValue )
  {
  }
}
