package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
@Injectable
@Named
public class Jsr330NamedInjectableModel
{
  Jsr330NamedInjectableModel( int someParam )
  {
  }
}
