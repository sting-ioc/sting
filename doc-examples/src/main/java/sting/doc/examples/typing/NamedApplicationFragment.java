package sting.doc.examples.typing;

import sting.Fragment;
import sting.Named;
import sting.Typed;

@Fragment
public interface NamedApplicationFragment
{
  @Named( "system" )
  @Typed( { MessageBroker.class, MessageSender.class } )
  default MessageService provideMessageService()
  {
    return new MessageService();
  }

  default LoginService provideLoginService( @Named( "system" ) MessageSender sender )
  {
    return new LoginService( sender );
  }

  default UserHeaderItem provideUserHeaderItem( @Named( "system" ) MessageBroker broker )
  {
    return new UserHeaderItem( broker );
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
