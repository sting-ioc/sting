package com.example.fragment.types;

import java.util.EventListener;
import java.util.Properties;

public class MyModel
  extends Properties
  implements Runnable, EventListener, AutoCloseable
{
  private static final long serialVersionUID = -3456430195886129035L;

  @Override
  public void run()
  {
  }

  @Override
  public void close()
  {
  }
}
