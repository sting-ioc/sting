package com.example.multiround.injectable;

import sting.Injector;

@Injector( includes = MyFragment.class )
interface MyInjector
{
  MyGeneratedInjectable getMyGeneratedInjectable();
}
