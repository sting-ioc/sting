package sting.doc.examples.autofragments;

import sting.ContributeTo;
import sting.Fragment;

@Fragment
@ContributeTo( "entities" )
public interface MessageRepositoryFragment
{
  default MessageRepository provideMessageRepository()
  {
    return new MessageRepository();
  }
}
