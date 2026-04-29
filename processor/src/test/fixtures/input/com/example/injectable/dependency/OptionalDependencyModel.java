package com.example.injectable.dependency;

import java.util.Optional;
import sting.Injectable;

@Injectable
public class OptionalDependencyModel
{
  OptionalDependencyModel( Optional<Runnable> runnable )
  {
  }
}
