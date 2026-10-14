package com.example.integration.todomvc.model;

import com.example.integration.todomvc.ArezComponent;
import org.jspecify.annotations.NullMarked;

@ArezComponent
@NullMarked
public abstract class TodoService {
    TodoService(final TodoRepository repository) {}
}
