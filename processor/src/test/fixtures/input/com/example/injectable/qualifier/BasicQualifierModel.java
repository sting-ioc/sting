package com.example.injectable.qualifier;

import sting.Injectable;
import sting.Service;

@Injectable( services = @Service( qualifier = "com.biz/SomeQualifier" ) )
public class BasicQualifierModel
{
}
