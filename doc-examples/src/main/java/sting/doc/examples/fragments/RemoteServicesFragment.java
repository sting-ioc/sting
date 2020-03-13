package sting.doc.examples.fragments;

import sting.Fragment;
import sting.Named;

@Fragment
public interface RemoteServicesFragment
{
  default UserServiceAsync getUserServiceAsync( @Named( "BaseURL" ) final String baseUrl )
  {
    final UserServiceAsync service = GWT.create( UserServiceAsync.class );
    ( (ServiceDefTarget) service ).setServiceEntryPoint( baseUrl + "api/rpc/user" );
    return service;
  }

  default AuthorizationServiceAsync getAuthorizationServiceAsync( @Named( "BaseURL" ) final String baseUrl )
  {
    final AuthorizationServiceAsync service = GWT.create( AuthorizationServiceAsync.class );
    ( (ServiceDefTarget) service ).setServiceEntryPoint( baseUrl + "api/rpc/authorization" );
    return service;
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
