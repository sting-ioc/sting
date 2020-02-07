package com.example.injectable.types;

import sting.Injectable;
import sting.Typed;

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
  @Injectable
  @Typed( { MyBaseInterface.class, MyOuterInterface.class } )
  public static class MyOuter
    implements MyBaseInterface
  {
  }
}
