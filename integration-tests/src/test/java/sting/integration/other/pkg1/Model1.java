package sting.integration.other.pkg1;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Typed;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg2.MyService3;

@Injectable
@Typed( { Object.class, Model1.class, MyService1.class, MyService2.class } )
public class Model1
  extends AbstractIntegrationTest.BaseModel
  implements MyService1, MyService2
{
  Model1( Collection<Supplier<MyService3>> service3Collection )
  {
  }
}
