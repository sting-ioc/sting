package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

final class Dependency
{
  /**
   * The type of the request.
   */
  @Nonnull
  private final RequestType _requestType;
  /**
   * The coordinate of the dependency to match.
   */
  @Nonnull
  private final Coordinate _coordinate;
  /**
   * Is the dependency optional.
   */
  private final boolean _optional;
  /**
   * The parameter that declares this dependency.
   * The parameter will either be a parameter on a @Provides annotated method, a parameter of
   * the constructor in an @Injectable annotated type or a parameter of the abstract method in
   * the @Factory annotated type.
   */
  @Nonnull
  private final VariableElement _requestElement;

  Dependency( @Nonnull final RequestType requestType,
              @Nonnull final Coordinate coordinate,
              final boolean optional,
              @Nonnull final VariableElement requestElement )
  {
    _requestType = Objects.requireNonNull( requestType );
    _coordinate = Objects.requireNonNull( coordinate );
    _optional = optional;
    _requestElement = Objects.requireNonNull( requestElement );
  }

  @Nonnull
  public RequestType getRequestType()
  {
    return _requestType;
  }

  @Nonnull
  public Coordinate getCoordinate()
  {
    return _coordinate;
  }

  public boolean isOptional()
  {
    return _optional;
  }

  @Nonnull
  public Element getRequestElement()
  {
    return _requestElement;
  }
}
