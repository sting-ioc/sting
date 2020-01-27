package sting.integration.other;

import java.util.Collection;
import org.testng.annotations.Test;
import sting.integration.AbstractIntegrationTest;
import sting.integration.other.pkg1.Model1;
import sting.integration.other.pkg1.Model2;
import sting.integration.other.pkg1.MyService1;
import sting.integration.other.pkg2.Model3;
import sting.integration.other.pkg2.Model4;
import sting.integration.other.pkg2.MyService3;
import static org.testng.Assert.*;

public final class PackageAccessInDifferentPackagesIntegrationTest
  extends AbstractIntegrationTest
{
  @Test
  public void scenario()
  {
    final MyInjector injector = MyInjector.create();

    assertCreateTrace( "" );

    final Model1 model1 = injector.getModel1();

    assertCreateTrace( "Model1[]" );

    final Model2 model2 = injector.getModel2();

    assertCreateTrace( "Model1[] Model2[() -> MyService2[], () -> MyService3[]]" );

    final Model3 model3 = injector.getModel3();

    assertCreateTrace( "Model1[] Model2[() -> MyService2[], () -> MyService3[]] Model3[[Model1, Model2]]" );

    final Model4 model4 = injector.getModel4();

    assertCreateTrace(
      "Model1[] Model2[() -> MyService2[], () -> MyService3[]] Model3[[Model1, Model2]] Model4[Collection<Supplier<MyService1>>{}]" );

    final Collection<MyService1> service2 = injector.getService1Collection();

    assertCreateTrace(
      "Model1[] Model2[() -> MyService2[], () -> MyService3[]] Model3[[Model1, Model2]] Model4[Collection<Supplier<MyService1>>{}]" );

    final MyService3 service3 = injector.getService3();

    assertCreateTrace(
      "Model1[] Model2[() -> MyService2[], () -> MyService3[]] Model3[[Model1, Model2]] Model4[Collection<Supplier<MyService1>>{}]" );

    final Collection<Object> objects = injector.getObjects();

    assertCreateTrace(
      "Model1[] Model2[() -> MyService2[], () -> MyService3[]] Model3[[Model1, Model2]] Model4[Collection<Supplier<MyService1>>{}]" );

    assertNotNull( service2 );
    assertNotNull( service3 );
    assertNotNull( model1 );
    assertNotNull( model2 );
    assertNotNull( model3 );
    assertNotNull( model4 );

    assertEquals( service2.size(), 2 );
    assertTrue( objects.contains( model1 ) );
    assertTrue( objects.contains( model2 ) );

    assertSame( service3, model3 );


    assertEquals( objects.size(), 4 );
    assertTrue( objects.contains( model1 ) );
    assertTrue( objects.contains( model2 ) );
    assertTrue( objects.contains( model3 ) );
    assertTrue( objects.contains( model4 ) );
  }
}
