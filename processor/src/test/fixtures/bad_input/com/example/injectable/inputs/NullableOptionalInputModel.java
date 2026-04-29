package com.example.injectable.inputs;

import java.util.Optional;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableOptionalInputModel
{
  NullableOptionalInputModel( @Nullable Optional<String> someValue )
  {
  }
}
