package com.example.integration.todomvc.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_Arez_TodoService {
  private Sting_Arez_TodoService() {
  }

  @Nonnull
  public static Object create(@Nonnull final TodoRepository repository) {
    return new Arez_TodoService( Objects.requireNonNull( repository ) );
  }
}
