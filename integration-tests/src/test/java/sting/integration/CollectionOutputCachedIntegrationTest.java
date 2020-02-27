package sting.integration;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import static org.testng.Assert.*;

public final class CollectionOutputCachedIntegrationTest
  extends AbstractIntegrationTest
{
  public interface MyType
  {
  }

  @Injectable
  @Typed( MyType.class )
  public static class Model1
    implements MyType
  {
  }

  @Injector
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new CollectionOutputCachedIntegrationTest_Sting_MyInjector();
    }

    Collection<MyType> getMyTypes();

    Collection<Supplier<MyType>> getMyTypeSuppliers();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();

    final Collection<MyType> myTypes1 = injector.getMyTypes();
    final Collection<MyType> myTypes2 = injector.getMyTypes();
    //noinspection SimplifiedTestNGAssertion
    assertTrue( myTypes1 == myTypes2 );

    final Collection<Supplier<MyType>> mySupplierTypes1 = injector.getMyTypeSuppliers();
    final Collection<Supplier<MyType>> mySupplierTypes2 = injector.getMyTypeSuppliers();
    //noinspection SimplifiedTestNGAssertion
    assertTrue( mySupplierTypes1 == mySupplierTypes2 );
  }
}
