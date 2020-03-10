package sting.doc.examples.typing.injectable;

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
