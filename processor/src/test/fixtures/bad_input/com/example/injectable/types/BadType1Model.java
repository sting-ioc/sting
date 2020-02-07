package com.example.injectable.types;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( Runnable.class )
public class BadType1Model
{
  BadType1Model()
  {
  }
}
