package com.example.injector.outputs;

import java.util.Optional;
import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface JavaOptionalOutputModel {
    Optional<MyModel> getMyModel();

    @Injectable
    class MyModel {}
}
