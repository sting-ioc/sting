package com.example.injector.outputs;

import java.io.IOException;
import sting.Injector;

@Injector
public interface MethodThrowsExceptionOutputModel
{
  String getMyThing()
    throws IOException;
}
