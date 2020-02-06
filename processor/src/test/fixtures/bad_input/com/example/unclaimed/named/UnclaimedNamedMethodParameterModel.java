package com.example.unclaimed.named;

import sting.Injectable;
import sting.Named;

@Injectable
public class UnclaimedNamedMethodParameterModel
{
  void myMethod( @Named( "blah" ) String someValue )
  {
  }
}
