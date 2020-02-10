package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_QualifiedOutputModel_Provider {
  @Nonnull
  default QualifiedOutputModel provide() {
    return new Sting_QualifiedOutputModel();
  }

  default QualifiedOutputModel.MyModel getMyModel(final QualifiedOutputModel injector) {
    return injector.getMyModel();
  }
}
