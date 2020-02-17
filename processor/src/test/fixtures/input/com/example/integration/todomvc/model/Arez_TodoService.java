package com.example.integration.todomvc.model;

import javax.annotation.Nonnull;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( TodoService.class )
final class Arez_TodoService
  extends TodoService
{
  Arez_TodoService( @Nonnull final TodoRepository repository )
  {
    super( repository );
  }
}
