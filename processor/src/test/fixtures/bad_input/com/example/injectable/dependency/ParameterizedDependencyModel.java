package com.example.injectable.dependency;

import java.util.function.Consumer;
import sting.Injectable;

@Injectable
public class ParameterizedDependencyModel
{
  ParameterizedDependencyModel( Consumer<String> consumer )
  {
  }
}
