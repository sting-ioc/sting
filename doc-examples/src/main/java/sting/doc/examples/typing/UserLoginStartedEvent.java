package sting.doc.examples.typing;

public class UserLoginStartedEvent
{
  private final String _username;

  public UserLoginStartedEvent( final String username )
  {
    _username = username;
  }

  public String getUsername()
  {
    return _username;
  }
}
