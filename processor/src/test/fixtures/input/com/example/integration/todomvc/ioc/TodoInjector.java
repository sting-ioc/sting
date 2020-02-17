package com.example.integration.todomvc.ioc;

import com.example.integration.todomvc.model.BrowserLocationFragment;
import com.example.integration.todomvc.model.TodoRepository;
import com.example.integration.todomvc.model.TodoService;
import com.example.integration.todomvc.model.ViewService;
import sting.Injector;

@Injector( includes = { TodoRepository.class,
                        BrowserLocationFragment.class,
                        TodoService.class,
                        ViewService.class } )
public interface TodoInjector
{
  TodoService getTodoService();

  ViewService getViewService();
}
