package sting.integration.other.pkg2;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;
import sting.Provides;
import sting.integration.other.pkg1.MyService1;

@Fragment
public interface MyPackageAccessFragment
{
  @Provides( types = { Object.class, Model3.class, MyService3.class, MyService4.class } )
  default Model3 provideModel3( Collection<MyService1> service3Collection )
  {
    return new Model3( service3Collection );
  }

  // Note: MysService3 not included in types
  @Provides( types = { Object.class, Model4.class, MyService4.class } )
  default Model4 provideModel4( Collection<Supplier<MyService1>> service1SupplierCollection )
  {
    return new Model4( service1SupplierCollection );
  }
}
