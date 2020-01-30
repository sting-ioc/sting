package com.example.unclaimed;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class UnclaimedMethodDependencyModel
{
  @Dependency
  String myMethod()
  {
    return "";
  }
}
