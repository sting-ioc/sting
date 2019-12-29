package com.example.at_inject;

import sting.Injectable;

@Injectable
public class SuppressedProtectedConstructorModel
{
  @SuppressWarnings( "Sting:ProtectedConstructor" )
  protected SuppressedProtectedConstructorModel()
  {
  }
}
