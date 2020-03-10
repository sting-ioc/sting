package sting.doc.examples.typing;

public interface AsyncCallback<T>
{
  void onFailure( Throwable caught );

  void onSuccess( T result );
}
