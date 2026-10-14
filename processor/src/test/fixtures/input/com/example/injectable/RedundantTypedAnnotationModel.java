package com.example.injectable;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(RedundantTypedAnnotationModel.class)
@org.jspecify.annotations.NullMarked
public class RedundantTypedAnnotationModel {
    RedundantTypedAnnotationModel() {}
}
