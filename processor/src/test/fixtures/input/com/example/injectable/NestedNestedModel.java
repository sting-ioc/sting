package com.example.injectable;

import sting.Injectable;

public class NestedNestedModel
{
  public static class Middle
  {
    @Injectable
    public static class MyModel
    {
    }
  }
}
