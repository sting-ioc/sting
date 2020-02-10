package com.example.injectable;

import javax.inject.Inject;
import sting.Injectable;

@Injectable
public class SuppressedJsr330InjectModel
{
  @SuppressWarnings( "Sting:Jsr330InjectPresent" )
  @Inject
  SuppressedJsr330InjectModel()
  {
  }
}
