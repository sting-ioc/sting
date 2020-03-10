package sting.doc.examples.typing.injectable;

public class UserLoginFailedEvent
{
  private final String _username;
  private final Throwable _error;

  public UserLoginFailedEvent( final String username, final Throwable error )
  {
    _username = username;
    _error = error;
  }

  public String getUsername()
  {
    return _username;
  }

  public Throwable getError()
  {
    return _error;
  }
}
