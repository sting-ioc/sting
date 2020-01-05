package com.example.injectable.dependency;

import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableDependencyModel
{
  NullableDependencyModel( @Nullable Runnable runnable )
  {
  }
}
