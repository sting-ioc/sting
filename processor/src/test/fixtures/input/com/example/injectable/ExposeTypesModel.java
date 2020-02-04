package com.example.injectable;

import sting.Injectable;
import sting.Service;

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

  @Injectable( services = { @Service( type = Object.class ),
                            @Service( type = MyMarkerInterface.class ),
                            @Service( type = MyMiddle.class ),
                            @Service( type = Runnable.class ),
                            @Service( type = MyOuter.class ),
                            @Service( type = MyBaseInterface.class ),
                            @Service( type = MyOuterInterface.class ) } )
  public static class MyOuter
    extends MyMiddle
    implements MyOuterInterface
  {
  }
}
