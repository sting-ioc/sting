package com.example.integration.todomvc.model;

import com.example.integration.todomvc.ArezComponent;
import javax.annotation.Nonnull;

@ArezComponent
public abstract class TodoService
{
  TodoService( @Nonnull final TodoRepository repository )
  {
  }
}
