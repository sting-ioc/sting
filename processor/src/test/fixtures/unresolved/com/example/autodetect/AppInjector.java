package com.example.autodetect;

import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface AppInjector
{
  LibModel getLibModel();
}
