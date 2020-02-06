package com.example.unclaimed.named;

import sting.Injectable;
import sting.Named;

@Injectable
public class UnclaimedNamedMethodModel
{
  @Named( "X" )
  String myMethod()
  {
    return "";
  }
}
