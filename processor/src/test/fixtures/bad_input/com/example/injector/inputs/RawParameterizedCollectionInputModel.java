package com.example.injector.inputs;

import java.util.Collection;
import java.util.List;
import sting.Injector;

@Injector
public interface RawParameterizedCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<List> getMyThing();
}
