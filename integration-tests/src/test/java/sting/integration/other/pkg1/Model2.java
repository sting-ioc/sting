package sting.integration.other.pkg1;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Typed;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg2.MyService3;

@Injectable
@Typed( { Object.class, Model2.class, MyService1.class } )
public class Model2
  extends AbstractIntegrationTest.BaseModel
  implements MyService1
{
  Model2( Supplier<MyService2> service2Supplier, Supplier<MyService3> service3Supplier )
  {
    super( "() -> MyService2[]", "() -> MyService3[]" );
  }
}
