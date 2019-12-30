package sting.processor;

enum RequestType
{
  /// A request for an instance of the dependency type T
  INSTANCE,
  /// A request for Supplier<T> of the dependency type T
  SUPPLIER
}
