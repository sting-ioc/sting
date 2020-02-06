package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Injectable;
import sting.Named;

@Injectable
public class ComplexDependencyModel
{
  ComplexDependencyModel( @Named( "lively" ) Runnable runnable,
                          @Nullable EventListener listener,
                          Serializable serializable,
                          @Named( "countDown" ) int countDown )
  {
  }
}
