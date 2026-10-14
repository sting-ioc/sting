package com.example.integration.todomvc.model;

import org.jspecify.annotations.NullMarked;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(TodoService.class)
@NullMarked
final class Arez_TodoService extends TodoService {
    Arez_TodoService(final TodoRepository repository) {
        super(repository);
    }
}
