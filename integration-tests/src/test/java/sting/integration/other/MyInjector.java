package sting.integration.other;

import java.util.Collection;
import javax.annotation.Nonnull;
import sting.Injector;
import sting.integration.other.pkg1.Model1;
import sting.integration.other.pkg1.Model2;
import sting.integration.other.pkg1.MyService1;
import sting.integration.other.pkg2.Model3;
import sting.integration.other.pkg2.Model4;
import sting.integration.other.pkg2.MyFragment;
import sting.integration.other.pkg2.MyService3;

@Injector( includes = { Model1.class, Model2.class, MyFragment.class } )
public interface MyInjector
{
  @Nonnull
  static MyInjector create()
  {
    return new Sting_MyInjector();
  }

  Model1 getModel1();

  Model2 getModel2();

  Model3 getModel3();

  Model4 getModel4();

  Collection<MyService1> getService1Collection();

  MyService3 getService3();

  Collection<Object> getObjects();
}
