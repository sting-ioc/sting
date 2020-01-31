package com.example.injector.outputs;

import java.util.function.Consumer;
import sting.Injector;

@Injector
public interface ParameterizedOutputModel
{
  Consumer<String> getMyThing();
}
