package com.example.integration.todomvc.ioc;

import com.example.integration.todomvc.model.TodoService;
import com.example.integration.todomvc.model.ViewService;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_TodoInjector_Provider implements Sting_TodoInjector_Provider {
  @Nonnull
  public TodoInjector $sting$_provide() {
    return provide();
  }

  public TodoService $sting$_getTodoService(final TodoInjector injector) {
    return getTodoService( Objects.requireNonNull( injector ) );
  }

  public ViewService $sting$_getViewService(final TodoInjector injector) {
    return getViewService( Objects.requireNonNull( injector ) );
  }
}
