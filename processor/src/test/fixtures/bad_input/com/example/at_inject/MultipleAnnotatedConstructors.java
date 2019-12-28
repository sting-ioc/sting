package com.example.at_inject;

import javax.inject.Inject;

public class MultipleAnnotatedConstructors
{
  @Inject
  MultipleAnnotatedConstructors( int someParam )
  {
  }

  MultipleAnnotatedConstructors()
  {
  }
}
