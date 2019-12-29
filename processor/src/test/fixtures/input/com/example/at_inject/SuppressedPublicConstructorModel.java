package com.example.at_inject;

import sting.Injectable;

@Injectable
public class SuppressedPublicConstructorModel
{
  @SuppressWarnings( "Sting:PublicConstructor" )
  public SuppressedPublicConstructorModel()
  {
  }
}
