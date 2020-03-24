package sting.doc.examples.dependency_kinds;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface ImageToolsFragment
{
  default ImageConverter provideImageConverter( Collection<ImageReader> readers,
                                                Collection<Supplier<ImageWriter>> writers )
  {
    return new ImageConverter( readers, writers );
  }

  //DOC ELIDE START
  //DOC ELIDE END
}
