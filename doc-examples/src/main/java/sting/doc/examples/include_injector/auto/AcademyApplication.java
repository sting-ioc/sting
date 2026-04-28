package sting.doc.examples.include_injector.auto;

import sting.Injector;
import sting.doc.examples.include_injector.BookCatalog;
import sting.doc.examples.include_injector.FinancialService;
import sting.doc.examples.include_injector.UserService;

@Injector(
  fragmentOnly = false,
  includes = { LibraryApplication.class,
               FinancialService.class,
               UserService.class }
)
public interface AcademyApplication
{
  BookCatalog getBookCatalog();

  UserService getUserService();
}
