package com.example.injectable.inputs;

import sting.Service;
import sting.Injectable;

@Injectable
public class IncompatibleTypeInputModel
{
  IncompatibleTypeInputModel( @Service( type = Runnable.class ) String someValue )
  {
  }
}
