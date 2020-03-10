package sting.doc.examples.typing.injectable;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface ApplicationFragment
{
  @Typed( { MessageBroker.class, MessageSender.class } )
  default MessageService provideMessageService()
  {
    return new MessageService();
  }

  default LoginService provideLoginService( MessageSender sender )
  {
    return new LoginService( sender );
  }

  default UserHeaderItem provideUserHeaderItem( MessageBroker broker )
  {
    return new UserHeaderItem( broker );
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
