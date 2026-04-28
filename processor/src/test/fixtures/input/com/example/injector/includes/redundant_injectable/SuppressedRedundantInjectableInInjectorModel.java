package com.example.injector.includes.redundant_injectable;

import sting.Injector;

@SuppressWarnings( { "Sting:AutoDiscoverableIncluded", "Sting:RedundantExplicitInjectableInclude" } )
@Injector( fragmentOnly = false, includes = { MyFragment.class, MyModel.class } )
interface SuppressedRedundantInjectableInInjectorModel
{
  MyModel getMyModel();
}
