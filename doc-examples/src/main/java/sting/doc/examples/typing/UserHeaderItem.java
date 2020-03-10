package sting.doc.examples.typing;

import sting.Injectable;

@Injectable
public class UserHeaderItem
{
  public UserHeaderItem( MessageBroker broker )
  {
    broker.addMessageListener( UserLoginStartedEvent.class,
                               e -> setText( "Authenticating " + e.getUsername() + " ..." ) );
    broker.addMessageListener( UserLoginCompletedEvent.class,
                               e -> setText( "User: " + e.getUsername() ) );
    broker.addMessageListener( UserLoginFailedEvent.class,
                               e -> setText( "Failed to authenticate " + e.getUsername() ) );
  }

  //DOC ELIDE START
  private void setText( final String text )
  {
  }
  //DOC ELIDE END
}
