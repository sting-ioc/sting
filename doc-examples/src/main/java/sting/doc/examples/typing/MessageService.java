package sting.doc.examples.typing;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( { MessageBroker.class, MessageSender.class } )
public class MessageService
  implements MessageSender, MessageBroker
{
  //DOC ELIDE START

  @Override
  public <T> void addMessageListener( final Class<T> type, final Listener<T> listener )
  {
  }

  @Override
  public void sendMessage( final Object message )
  {
  }
  //DOC ELIDE END
}
