package com.example.injectable.inputs;

import java.util.function.Consumer;
import sting.Injectable;

@Injectable
public class RawParameterizedInputModel
{
  @SuppressWarnings( "rawtypes" )
  RawParameterizedInputModel( Consumer consumer )
  {
  }
}
