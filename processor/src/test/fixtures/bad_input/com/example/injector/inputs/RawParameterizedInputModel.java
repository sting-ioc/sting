package com.example.injector.inputs;

import java.util.function.Consumer;
import sting.Injector;

@Injector
public interface RawParameterizedInputModel
{
  @SuppressWarnings( "rawtypes" )
  Consumer getMyThing();
}
