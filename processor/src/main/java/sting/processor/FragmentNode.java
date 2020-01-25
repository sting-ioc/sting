package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;

final class FragmentNode
{
  @Nonnull
  private final FragmentDescriptor _fragment;
  @Nonnull
  private final String _name;

  FragmentNode( @Nonnull final FragmentDescriptor fragment, @Nonnull final String name )
  {
    _fragment = Objects.requireNonNull( fragment );
    _name = Objects.requireNonNull( name );
  }

  @Nonnull
  FragmentDescriptor getFragment()
  {
    return _fragment;
  }

  @Nonnull
  String getName()
  {
    return _name;
  }
}
