package sting.doc.examples.getting_started.step1;

import sting.Injectable;

@Injectable
public class WelcomePage
{
  String render()
  {
    return "<h1>Welcome user to our wonderful application</h1>";
  }
}
