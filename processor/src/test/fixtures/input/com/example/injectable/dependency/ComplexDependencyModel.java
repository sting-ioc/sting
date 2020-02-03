package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Service;
import sting.Injectable;

@Injectable
public class ComplexDependencyModel
{
  ComplexDependencyModel( @Service( qualifier = "lively" ) Runnable runnable,
                          @Nullable EventListener listener,
                          Serializable serializable,
                          @Service( qualifier = "countDown" ) int countDown )
  {
  }
}
