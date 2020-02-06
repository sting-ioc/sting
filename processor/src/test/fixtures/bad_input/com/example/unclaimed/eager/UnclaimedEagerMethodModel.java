package com.example.unclaimed.eager;

import sting.Eager;

public interface UnclaimedEagerMethodModel
{
  @Eager
  default String foo()
  {
    return null;
  }
}
