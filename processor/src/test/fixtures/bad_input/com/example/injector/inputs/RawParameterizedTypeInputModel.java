package com.example.injector.inputs;

import java.util.List;
import sting.Service;
import sting.Injector;

@Injector( inputs = @Service( type = List.class ) )
interface RawParameterizedTypeInputModel
{
}
