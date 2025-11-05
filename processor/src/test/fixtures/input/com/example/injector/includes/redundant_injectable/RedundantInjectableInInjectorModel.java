package com.example.injector.includes.redundant_injectable;

import sting.Injector;

@SuppressWarnings( "Sting:AutoDiscoverableIncluded" )
@Injector( includes = { MyFragment.class, MyModel.class } )
interface RedundantInjectableInInjectorModel
{
  MyModel getMyModel();
}
