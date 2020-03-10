package sting.doc.examples.typing.injectable;

public interface RemoteLoginService
{
  void login( String username, String secret, AsyncCallback<Integer> callback );
}
