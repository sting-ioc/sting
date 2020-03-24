package sting.doc.examples.dependency_kinds;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class ImageConverter
{
  ImageConverter( Collection<ImageReader> readers,
                  Collection<Supplier<ImageWriter>> writers )
  {
    //DOC ELIDE START
    //DOC ELIDE END
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
