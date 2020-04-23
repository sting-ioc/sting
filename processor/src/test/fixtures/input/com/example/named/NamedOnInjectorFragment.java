package com.example.named;

import sting.InjectorFragment;
import sting.Named;

@InjectorFragment
interface NamedOnInjectorFragment
{
  @Named( "Foo" )
  String getConfig();
}
