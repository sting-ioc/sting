package com.example.injector.inputs;

import java.util.List;
import sting.Injector;

@Injector( inputs = @Injector.Service( type = List.class ) )
interface RawParameterizedTypeInputModel
{
}
