package com.example.injector.inputs;

import sting.Service;
import sting.Injector;

@SuppressWarnings( "DefaultAnnotationParam" )
@Injector( inputs = @Service( type = void.class ) )
interface VoidTypeInputModel
{
}
