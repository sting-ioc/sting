package sting.doc.examples.typing.injectable;

import sting.Injectable;

@Injectable
public class LoginService
{
  private final MessageSender _sender;
  //DOC ELIDE START
  //DOC ELIDE END

  LoginService( MessageSender sender )
  {
    _sender = sender;
    //DOC ELIDE START
    //DOC ELIDE END
  }

  public void requestLogin( String username, String secret )
  {
    _sender.sendMessage( new UserLoginStartedEvent( username ) );
    _remoteLoginService.login( username, secret, new AsyncCallback<Integer>()
    {
      @Override
      public void onFailure( final Throwable caught )
      {
        _sender.sendMessage( new UserLoginFailedEvent( username, caught ) );
      }

      @Override
      public void onSuccess( final Integer userId )
      {
        _sender.sendMessage( new UserLoginCompletedEvent( username, userId ) );
      }
    } );
  }
  //DOC ELIDE START
  private final RemoteLoginService _remoteLoginService = null;
  //DOC ELIDE END
}
