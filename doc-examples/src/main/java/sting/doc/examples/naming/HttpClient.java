package sting.doc.examples.naming;

import sting.Injectable;
import sting.Named;

@Injectable
public class HttpClient
{
  HttpClient( @Named( "cfg:hostname" ) String hostname,
              @Named( "cfg:port" ) int port,
              @Named( "cfg:connectTimeout" ) int connectTimeout,
              @Named( "cfg:username" ) String username,
              @Named( "cfg:password" ) String password )
  {
    //DOC ELIDE START
    //DOC ELIDE END
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
