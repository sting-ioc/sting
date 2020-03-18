package sting.doc.examples.eager;

import sting.Injector;

@Injector
public interface MyInjector
{
  MyComponent3 getMyComponent3();

  static MyInjector create()
  {
    return new Sting_MyInjector();
  }
}
