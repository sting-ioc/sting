package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings( { "CdiManagedBeanInconsistencyInspection", "Sting:Jsr330NamedPresent" } )
@Injectable
@Named
public class SuppressedJsr330NamedInjectableModel
{
  SuppressedJsr330NamedInjectableModel( int someParam )
  {
  }
}
