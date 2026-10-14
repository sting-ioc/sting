package com.example.integration.todomvc.model;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_Arez_ViewService {
  private Sting_Arez_ViewService() {
  }

  @SuppressWarnings("unchecked")
  public static Object create(final TodoRepository todoRepository, final Object browserLocation) {
    return new Arez_ViewService( todoRepository, (BrowserLocation) browserLocation );
  }
}
