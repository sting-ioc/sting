package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

@Injector(fragmentOnly = false, includes = ExplicitIncludesOfEnclosedInjectableModel.MyModel.class)
interface ExplicitIncludesOfEnclosedInjectableModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
