package com.example.fragment.dependency;

import java.util.EventListener;
import javax.annotation.Nullable;
import sting.Dependency;
import sting.Fragment;

@Fragment
public interface ComplexDependencyModel
{
  default Runnable provideRunnable( @Dependency( qualifier = "threadName" ) String name,
                                    int priority,
                                    @Nullable EventListener listener )
  {
    return null;
  }
}
