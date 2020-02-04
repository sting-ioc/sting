package com.example.injectable.types;

import java.util.concurrent.Callable;
import sting.Injectable;
import sting.Service;

@Injectable( services = { @Service( type = BadType2Model.class ),
                          @Service( type = Runnable.class ),
                          @Service( type = Object.class ),
                          @Service( type = Callable.class ) } )
public class BadType2Model
  implements Runnable
{
  BadType2Model()
  {
  }

  @Override
  public void run()
  {
  }
}
