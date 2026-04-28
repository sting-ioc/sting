package com.example.autodetect.provider;

import sting.Injector;

@Injector
interface AppInjector
{
  LibModel getLibModel();
}
