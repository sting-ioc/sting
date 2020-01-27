package sting.integration.other.pkg2;

import java.util.Collection;
import java.util.function.Supplier;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg1.MyService1;

public class Model4
  extends AbstractIntegrationTest.BaseModel
  implements MyService3, MyService4
{
  Model4( Collection<Supplier<MyService1>> service1SupplierCollection )
  {
    super( "Collection<Supplier<MyService1>>{}" );
  }
}
