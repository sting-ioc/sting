package sting.doc.examples.inputs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Injector;

@Injector( inputs = { @Injector.Input( type = AuthContext.class ),
                      @Injector.Input( type = LocalStorage.class, optional = true ),
                      @Injector.Input( type = String.class, qualifier = "hostname", optional = true ) } )
public interface MyInjector
{
  @Nonnull
  static MyInjector create( @Nonnull final AuthContext authContext,
                            @Nullable final LocalStorage localStorage,
                            @Nullable final String hostname )
  {
    return new Sting_MyInjector( authContext, localStorage, hostname );
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
