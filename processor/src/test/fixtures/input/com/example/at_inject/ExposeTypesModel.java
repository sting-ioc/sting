package com.example.at_inject;

import java.io.Serializable;
import sting.Injectable;

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

  @Injectable( types = { Object.class,
                         MyBase.class,
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
