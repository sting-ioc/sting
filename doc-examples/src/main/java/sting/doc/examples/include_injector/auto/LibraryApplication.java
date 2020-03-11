package sting.doc.examples.include_injector.auto;

import sting.Injector;
import sting.doc.examples.include_injector.BookCatalog;
import sting.doc.examples.include_injector.FinancialService;
import sting.doc.examples.include_injector.ResearchIndex;
import sting.doc.examples.include_injector.UserService;

@Injector(
  injectable = true,
  inputs = { @Injector.Input( type = FinancialService.class ),
             @Injector.Input( type = UserService.class ) }
)
public interface LibraryApplication
{
  BookCatalog getBookCatalog();

  ResearchIndex getResearchIndex();
}
