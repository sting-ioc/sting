package sting.processor;

import com.squareup.javapoet.ClassName;
import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

final class StingTypeNames
{
  @Nonnull
  static final ClassName COLLECTION = ClassName.get( Collection.class );
  @Nonnull
  static final ClassName SUPPLIER = ClassName.get( Supplier.class );

  private StingTypeNames()
  {
  }
}
