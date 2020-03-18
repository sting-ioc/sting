package sting.doc.examples.getting_started.step3;

import sting.Injector;

@Injector( includes = { AuthenticationService.class, WelcomePage.class } )
public interface WebApplication
{
  static WebApplication create()
  {
    return new Sting_WebApplication();
  }

  WelcomePage getWelcomePage();
}
