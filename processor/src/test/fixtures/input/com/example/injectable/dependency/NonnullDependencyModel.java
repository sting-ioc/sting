package com.example.injectable.dependency;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NonnullDependencyModel
{
  NonnullDependencyModel( @Nonnull Runnable runnable )
  {
  }
}
