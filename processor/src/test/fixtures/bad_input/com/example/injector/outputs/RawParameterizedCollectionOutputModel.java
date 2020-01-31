package com.example.injector.outputs;

import java.util.Collection;
import java.util.List;
import sting.Injector;

@Injector
public interface RawParameterizedCollectionOutputModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<List> getMyThing();
}
