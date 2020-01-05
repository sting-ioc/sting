package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injectable;

@Injectable
public class ComplexDependencyModel
{
  ComplexDependencyModel( @Dependency( qualifier = "lively" ) Runnable runnable,
                          @Nullable EventListener listener,
                          Serializable serializable,
                          @Dependency( qualifier = "countDown" ) int countDown )
  {
  }
}
