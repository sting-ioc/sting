package sting.doc.examples.typing.injectable;

public interface Listener<T>
{
  void onMessage( T message );
}
