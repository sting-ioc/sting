package com.example.injector.outputs;

import java.util.Collection;
import sting.Injector;

@Injector
public interface EmptyCollectionOutputModel
{
  Collection<Runnable> getRunnables();
}
