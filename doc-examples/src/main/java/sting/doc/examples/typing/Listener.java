package sting.doc.examples.typing;

public interface Listener<T>
{
  void onMessage( T message );
}
