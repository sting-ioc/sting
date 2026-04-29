package com.example.injector.outputs;

import java.util.Optional;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableOptionalOutputModel
{
  @Nullable
  Optional<String> getValue();
}
