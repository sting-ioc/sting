package com.example.injector.dependency;

import java.util.function.Consumer;
import sting.Injector;

@Injector
public interface RawParameterizedDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  Consumer getMyThing();
}
