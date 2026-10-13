package com.example.integration.todomvc.ioc;

import com.example.integration.todomvc.model.Sting_Arez_TodoRepository;
import com.example.integration.todomvc.model.Sting_Arez_TodoService;
import com.example.integration.todomvc.model.Sting_Arez_ViewService;
import com.example.integration.todomvc.model.Sting_BrowserLocationFragment;
import com.example.integration.todomvc.model.TodoRepository;
import com.example.integration.todomvc.model.TodoService;
import com.example.integration.todomvc.model.ViewService;
import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_TodoInjector implements TodoInjector {
  private final Sting_BrowserLocationFragment fragment1 = new Sting_BrowserLocationFragment();

  private @Nullable Object node1;

  private @Nullable Object node2;

  private final Object node3;

  private @Nullable Object node4;

  Sting_TodoInjector() {
    node3 = Objects.requireNonNull( fragment1.$sting$_createBrowserLocation() );
  }

  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( Sting_Arez_TodoRepository.create() );
    }
    assert null != node1;
    return node1;
  }

  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( Sting_Arez_TodoService.create((TodoRepository) node1()) );
    }
    assert null != node2;
    return node2;
  }

  @DoNotInline
  private synchronized Object node4() {
    if ( null == node4 ) {
      node4 = Objects.requireNonNull( Sting_Arez_ViewService.create((TodoRepository) node1(), node3) );
    }
    assert null != node4;
    return node4;
  }

  @Override
  public TodoService getTodoService() {
    return (TodoService) node2();
  }

  @Override
  public ViewService getViewService() {
    return (ViewService) node4();
  }
}
