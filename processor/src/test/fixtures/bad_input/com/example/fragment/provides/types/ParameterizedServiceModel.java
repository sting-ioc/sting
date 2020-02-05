package com.example.fragment.provides.types;

import java.util.ArrayList;
import java.util.Collection;
import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface ParameterizedServiceModel
{
  class Foo
    extends ArrayList<String>
  {
  }

  @Provides( services = @Service( type = Collection.class ) )
  default Foo provideX()
  {
    return null;
  }
}
