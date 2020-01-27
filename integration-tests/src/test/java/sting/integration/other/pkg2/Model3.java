package sting.integration.other.pkg2;

import java.util.Collection;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg1.MyService1;

public class Model3
  extends AbstractIntegrationTest.BaseModel
  implements MyService3, MyService4
{
  Model3( Collection<MyService1> service3Collection )
  {
    super( service3Collection );
  }
}
