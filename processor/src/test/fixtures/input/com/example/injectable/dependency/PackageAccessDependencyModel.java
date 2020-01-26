package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class PackageAccessDependencyModel
{
  interface MyType1
  {
  }

  interface MyType2
  {
  }

  interface MyType3
  {
  }

  interface MyType4
  {
  }

  PackageAccessDependencyModel( MyType1 instance,
                                Supplier<MyType2> supplier,
                                Collection<MyType3> collection,
                                Collection<Supplier<MyType4>> supplierCollection )
  {
  }
}
