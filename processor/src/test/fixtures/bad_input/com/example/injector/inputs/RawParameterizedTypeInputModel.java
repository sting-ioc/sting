package com.example.injector.inputs;

import java.util.List;
import sting.Injector;
import sting.Service;

@Injector( inputs = @Service( type = List.class ) )
interface RawParameterizedTypeInputModel
{
}
