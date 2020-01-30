package com.example.injector.inputs;

import java.util.Collection;
import sting.Injector;

@Injector
public interface RawCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  Collection getMyThing();
}
