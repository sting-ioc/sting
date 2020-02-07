package com.example.injector.inputs;

import sting.Injector;

@Injector( inputs = @Injector.Service( type = Runnable[].class ) )
interface ArrayTypeInputModel
{
}
