package com.example.injectable.types;

import java.util.EventListener;
import java.util.Properties;
import sting.Injectable;
import sting.Service;

// Does not expose BasicTypesModel or AutoCloseable nor Hashtable and related
@Injectable( services = { @Service( type = Runnable.class ),
                          @Service( type = EventListener.class ),
                          @Service( type = Object.class ),
                          @Service( type = Properties.class ) } )
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
