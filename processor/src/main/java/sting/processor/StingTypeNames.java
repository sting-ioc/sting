package sting.processor;

import com.squareup.javapoet.ClassName;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

final class StingTypeNames
{
  @Nonnull
  static final ClassName SUPPLIER = ClassName.get( Supplier.class );
}
