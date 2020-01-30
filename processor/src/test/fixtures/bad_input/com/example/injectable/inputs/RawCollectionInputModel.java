package com.example.injectable.inputs;

import java.util.Collection;
import sting.Injectable;

@Injectable
public class RawCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  RawCollectionInputModel( Collection someValue )
  {
  }
}
