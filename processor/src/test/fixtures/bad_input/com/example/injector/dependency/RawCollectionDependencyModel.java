package com.example.injector.dependency;

import java.util.Collection;
import sting.Injector;

@Injector
public interface RawCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  Collection getMyThing();
}
