package com.example.injector.inputs;

import sting.Injector;
import sting.Service;

@Injector( inputs = @Service( type = void.class ) )
interface VoidTypeInputModel
{
}
