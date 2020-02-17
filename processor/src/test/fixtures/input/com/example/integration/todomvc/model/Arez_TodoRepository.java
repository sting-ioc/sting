package com.example.integration.todomvc.model;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( TodoRepository.class )
final class Arez_TodoRepository
  extends TodoRepository
{
  Arez_TodoRepository()
  {
  }
}
