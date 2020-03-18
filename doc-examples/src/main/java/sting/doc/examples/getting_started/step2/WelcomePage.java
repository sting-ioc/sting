package sting.doc.examples.getting_started.step2;

import sting.Injectable;

@Injectable
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
