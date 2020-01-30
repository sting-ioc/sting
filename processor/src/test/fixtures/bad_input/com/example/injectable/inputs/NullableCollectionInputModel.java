package com.example.injectable.inputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableCollectionInputModel
{
  NullableCollectionInputModel( @Nullable Collection<String> someValue )
  {
  }
}
