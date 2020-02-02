package com.example.injector.inputs;

import sting.Dependency;
import sting.Injector;

@Injector( inputs = @Dependency( type = Runnable[].class ) )
interface ArrayTypeInputModel
{
}
