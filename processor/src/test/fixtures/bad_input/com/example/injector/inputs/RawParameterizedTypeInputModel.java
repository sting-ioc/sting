package com.example.injector.inputs;

import java.util.List;
import sting.Dependency;
import sting.Injector;

@Injector( inputs = @Dependency( type = List.class ) )
interface RawParameterizedTypeInputModel
{
}
