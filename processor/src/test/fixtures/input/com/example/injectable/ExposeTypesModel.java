package com.example.injectable;

import sting.Injectable;
import sting.Typed;

public class ExposeTypesModel
{
  public interface MyMarkerInterface
  {
  }

  public interface MyBaseInterface
  {
  }

  public interface MyOuterInterface
    extends MyBaseInterface
  {
  }

  public static class MyBase
    implements MyMarkerInterface
  {
  }

  public static class MyMiddle
    extends MyBase
    implements Runnable
  {
    @Override
    public void run()
    {
    }
  }

  @Injectable
  @Typed( { Object.class,
            MyMarkerInterface.class,
            MyMiddle.class,
            Runnable.class,
            MyOuter.class,
            MyBaseInterface.class,
            MyOuterInterface.class } )
  public static class MyOuter
    extends MyMiddle
    implements MyOuterInterface
  {
  }
}
