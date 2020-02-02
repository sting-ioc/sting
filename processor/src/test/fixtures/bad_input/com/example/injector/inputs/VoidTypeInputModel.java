package com.example.injector.inputs;

import sting.Dependency;
import sting.Injector;

@SuppressWarnings( "DefaultAnnotationParam" )
@Injector( inputs = @Dependency( type = void.class ) )
interface VoidTypeInputModel
{
}
