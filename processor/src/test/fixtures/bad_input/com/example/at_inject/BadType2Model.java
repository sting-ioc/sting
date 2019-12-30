package com.example.at_inject;

import java.util.concurrent.Callable;
import sting.Injectable;

@Injectable( types = { BadType2Model.class, Runnable.class, Object.class, Callable.class } )
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
