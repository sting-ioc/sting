package com.example.injector.includes.redundant_injectable;

import sting.Injector;

@SuppressWarnings( { "Sting:AutoDiscoverableIncluded", "Sting:RedundantExplicitInjectableInclude" } )
@Injector( includes = { MyFragment.class, MyModel.class } )
interface SuppressedRedundantInjectableInInjectorModel
{
  MyModel getMyModel();
}
