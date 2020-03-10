package sting.doc.examples.naming;

import sting.Injectable;
import sting.Named;
import sting.Typed;

@Injectable
@Named( "system:lighting" )
@Typed( SimulationSystem.class )
public class DynamicLightingSystem
  implements SimulationSystem
{
  //DOC ELIDE START
  //DOC ELIDE END
}
