package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class PublicAccessDependencyModel
{
  public interface MyType1
  {
  }

  public interface MyType2
  {
  }

  public interface MyType3
  {
  }

  public interface MyType4
  {
  }

  PublicAccessDependencyModel( MyType1 instance,
                               Supplier<MyType2> supplier,
                               Collection<MyType3> collection,
                               Collection<Supplier<MyType4>> supplierCollection )
  {
  }
}
