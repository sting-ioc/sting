package com.example.dependency;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class UnclaimedMethodParameterDependencyModel
{
  void myMethod( @Dependency String someValue )
  {
  }
}
