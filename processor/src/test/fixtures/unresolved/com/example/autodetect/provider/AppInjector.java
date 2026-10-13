package com.example.autodetect.provider;

import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface AppInjector
{
  LibModel getLibModel();
}
