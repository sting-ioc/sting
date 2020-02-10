package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
@Injectable
public class Jsr330NamedInputModel
{
  Jsr330NamedInputModel( @Named int someParam )
  {
  }
}
