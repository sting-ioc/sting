package sting.integration.other.pkg1;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Service;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg2.MyService3;

@Injectable( services = { @Service( type = Object.class ),
                          @Service( type = Model1.class ),
                          @Service( type = MyService1.class ),
                          @Service( type = MyService2.class ) } )
public class Model1
  extends AbstractIntegrationTest.BaseModel
  implements MyService1, MyService2
{
  Model1( Collection<Supplier<MyService3>> service3Collection )
  {
  }
}
