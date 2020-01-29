package com.example.dependency;

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
