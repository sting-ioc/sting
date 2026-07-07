package com.example.injectable;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(RedundantTypedAnnotationModel.class)
public class RedundantTypedAnnotationModel {
    RedundantTypedAnnotationModel() {}
}
