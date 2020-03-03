package com.example.multiround;

import sting.Injector;

@Injector( includes = MyFragment.class )
interface MyInjector
{
  Object getObject();
}
