package sting.doc.examples.naming;

import sting.Injectable;
import sting.Named;

@Injectable
public class GeometryProcessor
{
  GeometryProcessor( @Named( "system:particleSystem" ) SimulationSystem particleSystem,
                     @Named( "system:lighting" ) SimulationSystem lighting )
  {
    //DOC ELIDE START
    //DOC ELIDE END
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
