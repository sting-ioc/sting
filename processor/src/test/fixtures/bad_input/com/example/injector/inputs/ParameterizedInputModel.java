package com.example.injector.inputs;

import java.util.function.Consumer;
import sting.Injector;

@Injector
public interface ParameterizedInputModel
{
  Consumer<String> getMyThing();
}
