package com.example.injectable;

import sting.Eager;
import sting.Injectable;
import sting.Named;
import sting.Typed;

@Injectable
@Named( "X" )
@Typed( {} )
@Eager
class QualifiedWithNoTypesModel
{
}
