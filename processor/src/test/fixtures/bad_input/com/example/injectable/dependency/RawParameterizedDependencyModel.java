package com.example.injectable.dependency;

import java.util.function.Consumer;
import sting.Injectable;

@Injectable
public class RawParameterizedDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  RawParameterizedDependencyModel( Consumer consumer )
  {
  }
}
