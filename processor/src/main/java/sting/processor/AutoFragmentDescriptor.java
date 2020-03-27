package sting.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

final class AutoFragmentDescriptor
{
  /**
   * The unique key for the auto-fragment.
   */
  @Nonnull
  private final String _key;
  /**
   * The element declaring the auto-fragment.
   * It must be an interface.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * The list of types contributing to the fragment.
   */
  @Nonnull
  private final Collection<ContributorDescriptor> _contributors = new HashSet<>();
  /**
   * True if the fragment has been generated.
   */
  private boolean _fragmentGenerated;
  /**
   * Has the auto-fragment been modified in the current round.
   */
  private boolean _modified;
  /**
   * Are any of the contributors auto-discoverable.
   * If they are we should suppress the warning when generating the fragment.
   * It is expected that AutoFragments when generated should never cause code
   * warnings in generated code.
   */
  private boolean _autoDiscoverableContributors;

  AutoFragmentDescriptor( @Nonnull final String key, @Nonnull final TypeElement element )
  {
    _key = Objects.requireNonNull( key );
    assert ElementKind.INTERFACE == element.getKind();
    _element = Objects.requireNonNull( element );
  }

  @Nonnull
  String getKey()
  {
    return _key;
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  Collection<ContributorDescriptor> getContributors()
  {
    return _contributors;
  }

  boolean isModified()
  {
    return _modified;
  }

  void resetModified()
  {
    _modified = false;
  }

  void markAsModified()
  {
    _modified = true;
  }

  boolean isFragmentGenerated()
  {
    return _fragmentGenerated;
  }

  void markFragmentGenerated()
  {
    _fragmentGenerated = true;
  }

  boolean hasAutoDiscoverableContributors()
  {
    return _autoDiscoverableContributors;
  }

  void markAsAutoDiscoverableContributors()
  {
    _autoDiscoverableContributors = true;
  }
}
