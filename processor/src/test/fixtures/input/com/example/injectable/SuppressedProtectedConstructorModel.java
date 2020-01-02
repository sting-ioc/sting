package com.example.injectable;

import sting.Injectable;

@Injectable
public class SuppressedProtectedConstructorModel
{
  @SuppressWarnings( "Sting:ProtectedConstructor" )
  protected SuppressedProtectedConstructorModel()
  {
  }
}
