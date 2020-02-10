package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
@Injectable
public class SuppressedJsr330NamedInputModel
{
  @SuppressWarnings( "Sting:Jsr330NamedPresent" )
  SuppressedJsr330NamedInputModel( @Named int someParam )
  {
  }
}
