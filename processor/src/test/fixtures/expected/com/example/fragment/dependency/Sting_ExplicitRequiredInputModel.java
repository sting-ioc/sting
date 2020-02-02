package com.example.fragment.dependency;

import java.util.Objects;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_ExplicitRequiredInputModel implements ExplicitRequiredInputModel {
  public Runnable $sting$_provideRunnable(final String name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
