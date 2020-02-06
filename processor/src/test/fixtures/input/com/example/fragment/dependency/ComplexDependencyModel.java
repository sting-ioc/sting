package com.example.fragment.dependency;

import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Named;

@Fragment
public interface ComplexDependencyModel
{
  default Runnable provideRunnable( @Named( "threadName" ) String name,
                                    int priority,
                                    @Nullable EventListener listener )
  {
    return null;
  }
}
