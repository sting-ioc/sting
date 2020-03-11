package sting.doc.examples.include_injector.manual;

import sting.Fragment;
import sting.doc.examples.include_injector.BookCatalog;
import sting.doc.examples.include_injector.FinancialService;
import sting.doc.examples.include_injector.ResearchIndex;
import sting.doc.examples.include_injector.UserService;

@Fragment
public interface LibraryApplicationFragment
{
  default LibraryApplication provideLibraryApplication( FinancialService financialService,
                                                        UserService userService )
  {
    return new Sting_LibraryApplication( financialService, userService );
  }

  default BookCatalog provideBookCatalog( LibraryApplication injector )
  {
    return injector.getBookCatalog();
  }

  default ResearchIndex provideResearchIndex( LibraryApplication injector )
  {
    return injector.getResearchIndex();
  }
}
