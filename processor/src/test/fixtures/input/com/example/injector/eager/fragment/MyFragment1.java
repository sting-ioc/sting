package com.example.injector.eager.fragment;

import sting.Fragment;

@Fragment( includes = { MyFragment2.class, MyModel2.class } )
public interface MyFragment1
{
}
