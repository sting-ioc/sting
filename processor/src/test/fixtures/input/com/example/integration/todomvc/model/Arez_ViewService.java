package com.example.integration.todomvc.model;

import org.jspecify.annotations.NullMarked;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(ViewService.class)
@NullMarked
final class Arez_ViewService extends ViewService {
    Arez_ViewService(final TodoRepository todoRepository, final BrowserLocation browserLocation) {
        super(todoRepository, browserLocation);
    }
}
