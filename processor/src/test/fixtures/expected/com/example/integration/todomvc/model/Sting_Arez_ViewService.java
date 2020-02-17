package com.example.integration.todomvc.model;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Arez_ViewService {
  private Sting_Arez_ViewService() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static Object create(@Nonnull final TodoRepository todoRepository,
      @Nonnull final Object browserLocation) {
    return new Arez_ViewService( Objects.requireNonNull( todoRepository ), Objects.requireNonNull( (BrowserLocation) browserLocation ) );
  }
}
