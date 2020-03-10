package sting.doc.examples.typing.injectable;

public class UserLoginCompletedEvent
{
  private final String _username;
  private final int _userId;

  public UserLoginCompletedEvent( final String username, final int userId )
  {
    _username = username;
    _userId = userId;
  }

  public String getUsername()
  {
    return _username;
  }

  public int getUserId()
  {
    return _userId;
  }
}
