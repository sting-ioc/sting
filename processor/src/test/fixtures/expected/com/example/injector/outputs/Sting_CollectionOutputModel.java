package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_CollectionOutputModel implements CollectionOutputModel {
  private @Nullable Object node1;

  private @Nullable Collection<CollectionOutputModel.MyModel> $sting$_getMyModelCache;

  Sting_CollectionOutputModel() {
  }

  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = CollectionOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Collection<CollectionOutputModel.MyModel> getMyModel() {
    if ( null == $sting$_getMyModelCache ) {
      $sting$_getMyModelCache = Collections.singletonList( (CollectionOutputModel.MyModel) node1() );
    }
    return $sting$_getMyModelCache;
  }
}
