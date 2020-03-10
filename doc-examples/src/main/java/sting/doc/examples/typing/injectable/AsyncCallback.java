package sting.doc.examples.typing.injectable;

public interface AsyncCallback<T>
{
  void onFailure( Throwable caught );

  void onSuccess( T result );
}
