package sting.doc.examples.getting_started.step3;

import sting.Eager;
import sting.Injectable;

@Injectable
@Eager
public class WelcomePage
{
  private final AuthenticationService _authenticationService;

  WelcomePage( AuthenticationService authenticationService )
  {
    _authenticationService = authenticationService;
  }

  String render()
  {
    return "<h1>Welcome " + _authenticationService.currentUsername() +
           " to our wonderful application</h1>";
  }
}
