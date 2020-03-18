package sting.doc.examples.eager;

import java.util.function.Supplier;
import sting.Eager;
import sting.Injectable;

@Injectable
@Eager
public class MyComponent3
{
  private final MyComponent1 _myComponent1;
  private final Supplier<MyComponent2> _myComponent2Supplier;

  MyComponent3( MyComponent1 myComponent1,
                Supplier<MyComponent2> myComponent2Supplier )
  {
    _myComponent1 = myComponent1;
    _myComponent2Supplier = myComponent2Supplier;
    //DOC ELIDE START
    //DOC ELIDE END
  }

  //DOC ELIDE START
  //DOC ELIDE END
  public void performAction()
  {
    _myComponent2Supplier.get().calculate();
  }

  //DOC ELIDE START
  //DOC ELIDE END
}
