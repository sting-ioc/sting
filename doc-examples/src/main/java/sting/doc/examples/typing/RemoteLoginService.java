package sting.doc.examples.typing;

public interface RemoteLoginService
{
  void login( String username, String secret, AsyncCallback<Integer> callback );
}
