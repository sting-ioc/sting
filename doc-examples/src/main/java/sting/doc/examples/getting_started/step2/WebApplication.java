package sting.doc.examples.getting_started.step2;

import sting.Injector;

@Injector( includes = { AuthenticationService.class, WelcomePage.class } )
public interface WebApplication
{
  WelcomePage getWelcomePage();
}
