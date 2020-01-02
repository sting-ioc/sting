package com.example.injectable;

import sting.Injectable;

@Injectable
public class SuppressedPublicConstructorModel
{
  @SuppressWarnings( "Sting:PublicConstructor" )
  public SuppressedPublicConstructorModel()
  {
  }
}
