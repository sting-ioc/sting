package com.example.injectable.types;

import sting.Injectable;
import sting.Service;

public class BadType3Model
{
  public interface MyBaseInterface
  {
  }

  public interface MyOuterInterface
    extends MyBaseInterface
  {
  }

  // Does not implement MyOuterInterface!
  @Injectable( services = { @Service( type = MyBaseInterface.class ), @Service( type = MyOuterInterface.class ) } )
  public static class MyOuter
    implements MyBaseInterface
  {
  }
}
