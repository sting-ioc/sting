package com.example.injectable.dependency;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableCollectionDependencyModel
{
  NullableCollectionDependencyModel( @Nullable Collection<String> someValue )
  {
  }
}
