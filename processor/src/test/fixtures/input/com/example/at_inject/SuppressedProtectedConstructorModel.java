package com.example.at_inject;

import javax.inject.Inject;

public class SuppressedProtectedConstructorModel
{
  @SuppressWarnings( "Sting:ProtectedConstructor" )
  @Inject
  protected SuppressedProtectedConstructorModel()
  {
  }
}
