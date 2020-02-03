package com.example.unclaimed;

import sting.Service;
import sting.Injectable;

@Injectable
public class UnclaimedMethodDependencyModel
{
  @Service
  String myMethod()
  {
    return "";
  }
}
