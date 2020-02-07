package com.example.injector.inputs;

import sting.Injector;
import sting.Service;

@SuppressWarnings( "DefaultAnnotationParam" )
@Injector( inputs = @Service( type = void.class ) )
interface VoidTypeInputModel
{
}
