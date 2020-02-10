package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_OptionalMissingOutputModel_Provider implements Sting_OptionalMissingOutputModel_Provider {
  @Nonnull
  public OptionalMissingOutputModel $sting$_provide() {
    return provide();
  }

  @Nullable
  public OptionalMissingOutputModel.MyModel1 $sting$_getMyModel1(
      final OptionalMissingOutputModel injector) {
    return getMyModel1( Objects.requireNonNull( injector ) );
  }

  public OptionalMissingOutputModel.MyModel2 $sting$_getMyModel2(
      final OptionalMissingOutputModel injector) {
    return getMyModel2( Objects.requireNonNull( injector ) );
  }
}
