package com.example.fragment.dependency;

import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Service;
import sting.Fragment;

@Fragment
public interface ComplexDependencyModel
{
  default Runnable provideRunnable( @Service( qualifier = "threadName" ) String name,
                                    int priority,
                                    @Nullable EventListener listener )
  {
    return null;
  }
}
