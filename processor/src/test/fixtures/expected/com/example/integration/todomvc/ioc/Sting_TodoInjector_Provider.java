package com.example.integration.todomvc.ioc;

import com.example.integration.todomvc.model.TodoService;
import com.example.integration.todomvc.model.ViewService;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_TodoInjector_Provider {
  @Nonnull
  default TodoInjector provide() {
    return new Sting_TodoInjector();
  }

  default TodoService getTodoService(final TodoInjector injector) {
    return injector.getTodoService();
  }

  default ViewService getViewService(final TodoInjector injector) {
    return injector.getViewService();
  }
}
