package com.example.injector.outputs;

import java.util.function.Consumer;
import sting.Injector;

@Injector
public interface RawParameterizedOutputModel
{
  @SuppressWarnings( "rawtypes" )
  Consumer getMyThing();
}
