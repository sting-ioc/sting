package sting;

/**
 * Provides instances of {@code T} and is implemented by the injector. For
 * any type {@code T} that can be injected, you can also inject
 * {@code Provider<T>}. Compared to injecting {@code T} directly, injecting
 * {@code Provider<T>} enables:
 *
 * <ul>
 *   <li>lazy retrieval of an instance.</li>
 *   <li>breaking circular dependencies.</li>
 * </ul>
 *
 * @param <T> the type of the instance returned by the provider.
 */
public interface Provider<T>
{
  /**
   * Provides a fully-constructed and injected instance of {@code T}.
   *
   * @return a fully-constructed and injected instance of {@code T}.
   */
  T get();
}
