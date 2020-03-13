package com.example.multiround.fragment;

import sting.Injector;

@Injector( includes = MyFragment.class )
interface MyInjector
{
  Object getObject();
}
