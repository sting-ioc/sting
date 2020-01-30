package com.example.injectable.inputs;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class IncompatibleTypeInputModel
{
  IncompatibleTypeInputModel( @Dependency( type = Runnable.class ) String someValue )
  {
  }
}
