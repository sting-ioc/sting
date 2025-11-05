package com.example.fragment.includes.cycle;

import sting.Fragment;

public interface IncludeLongerCycleModel
{
  @Fragment( includes = B.class )
  interface A {}

  @Fragment( includes = C.class )
  interface B {}

  @Fragment( includes = A.class )
  interface C {}
}
