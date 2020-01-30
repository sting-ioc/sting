package com.example.injectable.dependency;

import javax.annotation.Nonnull;
import sting.Injectable;

@Injectable
public class NonnullDependencyModel
{
  NonnullDependencyModel( @Nonnull Runnable runnable )
  {
  }
}
