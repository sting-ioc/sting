package com.example.unclaimed;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class UnclaimedMethodInputModel
{
  void myMethod( @Dependency String someValue )
  {
  }
}
