package com.example.injector.outputs;

import java.util.Optional;
import sting.Injectable;
import sting.Injector;

@Injector
interface JavaOptionalOutputModel
{
  Optional<MyModel> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
