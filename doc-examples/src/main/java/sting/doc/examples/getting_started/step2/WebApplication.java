package sting.doc.examples.getting_started.step2;

import sting.Injector;

@Injector( fragmentOnly = false, includes = { AuthenticationService.class, WelcomePage.class } )
public interface WebApplication
{
  WelcomePage getWelcomePage();
}
