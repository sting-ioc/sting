package sting.processor;

enum ResolveType
{
  // Resolved and ready to go
  RESOLVED,
  // The java class is resolved but the descriptors were not present or in the wrong format
  // This can occur depending on which order the files are processed within a single round
  MAYBE_UNRESOLVED,
  // The java class interface is not resolved
  UNRESOLVED
}
