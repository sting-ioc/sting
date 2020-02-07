package com.example.fragment.provides.types;

import java.util.ArrayList;
import java.util.Collection;
import sting.Fragment;
import sting.Typed;

@Fragment
public interface ParameterizedServiceModel
{
  class Foo
    extends ArrayList<String>
  {
  }

  @Typed( Collection.class )
  default Foo provideX()
  {
    return null;
  }
}
