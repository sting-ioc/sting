package sting;

/**
 * Enum describing whether a service is required or optional.
 */
public enum NecessityType
{
  /**
   * The service is required.
   *
   * <p>If the {@link Service} annotation is describing an input into the service and the injector can
   * not supply it then compilation of the injector should fail. If the {@link Service} annotation is
   * describing a service provided then the service is always provided.</p>
   *
   * <p>The associated parameter or method may be annotated with {@link javax.annotation.Nonnull}
   * and must not be annotated with {@link javax.annotation.Nullable}.</p>
   */
  REQUIRED,

  /**
   * The service is optional.
   *
   * <p>If the {@link Service} annotation is describing a service provided then the {@link Provides}
   * method may return a null. If the {@link Service} annotation is describing an input into a service
   * then the input may be null. The value must not be a nullable value (i.e. not a primitive).</p>
   *
   * <p>The associated parameter or method should be annotated with {@link javax.annotation.Nullable}
   * and must not be annotated with {@link javax.annotation.Nonnull}.</p>
   */
  OPTIONAL,

  /**
   * The service is required or optional based on heuristics.
   *
   * <p>If the parameter or method is annotated with {@link javax.annotation.Nullable} and the type is
    * not primitive, not a {@link java.util.function.Supplier} nor a {@link java.util.Collection} then the
    * service is considered {@link #OPTIONAL} otherwise it is considered {@link #REQUIRED}.</p>
   */
  AUTODETECT
}
