package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

final class ContributorDescriptor
{
  /**
   * The unique key for the auto-fragment that contributing to.
   */
  @Nonnull
  private final String _key;
  /**
   * The element declared as a contributor.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * Is the associated TypeElement auto-discoverable
   */
  private final boolean _autoDiscoverable;

  ContributorDescriptor( @Nonnull final String key, @Nonnull final TypeElement element )
  {
    this( key, element, false );
  }

  ContributorDescriptor( @Nonnull final String key, @Nonnull final TypeElement element, final boolean autoDiscoverable )
  {
    _key = Objects.requireNonNull( key );
    _element = Objects.requireNonNull( element );
    _autoDiscoverable = autoDiscoverable;
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
}
