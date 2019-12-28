package com.example.at_inject;

import javax.inject.Inject;

public class SuppressedPublicConstructorModel
{
  @SuppressWarnings( "Sting:PublicConstructor" )
  @Inject
  public SuppressedPublicConstructorModel()
  {
  }
}
