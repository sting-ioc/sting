package com.example.injectable.inputs;

import java.util.function.Consumer;
import sting.Injectable;

@Injectable
public class ParameterizedInputModel
{
  ParameterizedInputModel( Consumer<String> consumer )
  {
  }
}
