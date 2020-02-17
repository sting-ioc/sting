package com.example.integration.todomvc.model;

import javax.annotation.Nonnull;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( ViewService.class )
final class Arez_ViewService
  extends ViewService
{
  Arez_ViewService( @Nonnull final TodoRepository todoRepository, @Nonnull final BrowserLocation browserLocation )
  {
    super( todoRepository, browserLocation );
  }
}
