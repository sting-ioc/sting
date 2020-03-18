package sting.doc.examples.getting_started.step3;

public class Main
{
  public static void main( String[] args )
  {
    // Instantiate the injector
    final WebApplication application = WebApplication.create();

    //DOC ELIDE START
    //DOC ELIDE END

    // Instantiate the WelcomePage component and use it
    final WelcomePage page = application.getWelcomePage();
    final String welcomeHtml = page.render();
    //DOC ELIDE START
    //DOC ELIDE END
  }
}
