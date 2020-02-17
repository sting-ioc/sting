package com.example.integration.todomvc.model;

import com.example.integration.todomvc.ArezComponent;
import javax.annotation.Nonnull;

@ArezComponent
public abstract class ViewService
{
  ViewService( @Nonnull final TodoRepository todoRepository, @Nonnull final BrowserLocation browserLocation )
  {
  }
}
