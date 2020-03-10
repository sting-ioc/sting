package sting.doc.examples.typing;

public interface MessageBroker
{
  <T> void addMessageListener( Class<T> type, Listener<T> listener );
  //DOC ELIDE START
  //DOC ELIDE END
}
