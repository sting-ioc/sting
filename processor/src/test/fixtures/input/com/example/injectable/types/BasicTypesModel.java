package com.example.injectable.types;

import java.util.EventListener;
import java.util.Properties;
import sting.Injectable;

// Does not expose BasicTypesModel or AutoCloseable nor Hashtable and related
@Injectable( types = { Runnable.class, EventListener.class, Object.class, Properties.class } )
public class BasicTypesModel
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
