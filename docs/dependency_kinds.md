---
title: Kinds of Dependencies
---

Sting allows a component to declare different kinds of dependencies. The kind determines the manner in which
Sting supplies the dependency to the component. The kind is specified by the java type of the constructor
parameter (for {@link: sting.Injectable} annotated types), the method parameter (for methods contained in
{@link: sting.Fragment} annotated types) or the method return value (for injector [outputs](outputs.md)).
An outer `@org.jspecify.annotations.Nullable` annotation makes an instance dependency optional.
Unannotated instance requests remain required, including in unmarked consumer code.

The following table lists the different kinds supported by Sting.

<table>
  <caption align="bottom">Different Kinds of Dependencies</caption>
  <thead>
  <tr>
    <th>Kind</th>
    <th>Java Type</th>
    <th>Description</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>Instance</td>
    <td><code>T</code></td>
    <td>
      Sting provides a single component that publishes the type <code>T</code>. The Sting annotation
      processor will generate an error if there are multiple components that publishes a service of
      type <code>T</code> as it is unable to determine which one needs to be supplied to satisfy the
      dependency.
    </td>
  </tr>
  <tr>
    <td>Nullable Instance</td>
    <td nowrap><code>@Nullable T</code></td>
    <td>
      Sting provides a single component that publishes the type <code>T</code> if the instance is
      included in the injector either via auto-discovery or via explicit includes. If no such component
      is present then sting will supply a <code>null</code> value.
    </td>
  </tr>
  <tr>
    <td>Optional Instance</td>
    <td nowrap><code>Optional&lt;T&gt;</code></td>
    <td>
      Sting provides an <code>Optional</code> containing the single matching service if present.
      Missing bindings and nullable providers that return <code>null</code> both result in
      <code>Optional.empty()</code>. As with nullable instance dependencies, multiple matching
      bindings are still rejected.
    </td>
  </tr>
  <tr>
    <td>Supplier</td>
    <td nowrap><code>Supplier&lt;T&gt;</code></td>
    <td>
      Sting provides a supplier instance that can be invoked to return a single component that publishes
      the type <em>T</em>. As with the "Instance" kind, the Sting annotation processor will generate
      an error if there are multiple components that publishes a service of type <code>T</code>. Sting
      requires that the component not invoke the supplier to access the dependency until the injector is
      completely initialized otherwise errors may be generated. It is likely that this will be enforcable
      in a later version of Sting. A supplier dependency means the Sting annotation processor need not
      order the dependency prior to the consumer component and will not reject the dependency due to it
      forming a dependency loop.
    </td>
  </tr>
  <tr>
    <td>Optional Supplier</td>
    <td nowrap><code>Supplier&lt;Optional&lt;T&gt;&gt;</code></td>
    <td>
      Sting provides a lazy supplier that returns the single matching service wrapped in an
      <code>Optional</code>. Missing bindings and nullable providers that return <code>null</code>
      both yield <code>Optional.empty()</code>.
    </td>
  </tr>
  <tr>
    <td>Collection</td>
    <td nowrap><code>Collection&lt;T&gt;</code></td>
    <td>
      Sting provides a read-only collection containing every component that publishes
      the type <code>T</code>. Optional bindings are permitted for this request shape and
      any optional binding that produces <code>null</code> is simply omitted from the
      resulting collection. This is primarily aimed at systems that support multiple
      plugins of the same type.
    </td>
  </tr>
  <tr>
    <td>Supplier Collection</td>
    <td nowrap><code>Collection&lt;Supplier&lt;T&gt;&gt;</code></td>
    <td>
      Sting provides a read-only collection containing a supplier for every component that publishes
      the type <code>T</code>. This is primarily aimed at systems that support multiple
      plugins of the same type and either the plugins can be lazily created when required
      or a plugin may have a direct dependency on the component declaring the dependency.
    </td>
  </tr>
  <tr>
    <td>Optional Supplier Collection</td>
    <td nowrap><code>Collection&lt;Supplier&lt;Optional&lt;T&gt;&gt;&gt;</code></td>
    <td>
      Sting provides a read-only collection containing a supplier for every component that
      publishes the type <code>T</code>, with each supplier returning its value wrapped in
      an <code>Optional</code>. This preserves laziness while allowing nullable bindings and
      missing values to surface as <code>Optional.empty()</code>.
    </td>
  </tr>
  </tbody>
</table>

### Examples

The `ErrorResponder` includes an "instance" dependency on the `NotificationService` service so that
the component can provide feedback to the user when an error occurs. It also includes a "nullable instance"
dependency on the `FaultAuditService` which it will use to report the error to a fault monitoring service.
In local and non-production environments, the injector will not bind a component that publishes the
`FaultAuditService` and the injector will pass a null value into the component.

{@file_content: file=sting/doc/examples/dependency_kinds/ErrorResponder.java start_line=@Injectable}

The `ImageConverter` just shows collection dependencies as it assumes the component is capable of
reading in one image format and writing out another image format.

{@file_content: file=sting/doc/examples/dependency_kinds/ImageConverter.java start_line=@Injectable}

The `ImageConverter` component could have easily be returned by a provider method defined in a
fragment such as:

{@file_content: file=sting/doc/examples/dependency_kinds/ImageToolsFragment.java start_line=@Fragment}

The service could also be retrieved as [outputs](outputs.md) using different dependency kinds:

{@file_content: file=sting/doc/examples/dependency_kinds/MyInjector.java start_line=@Injector}

## Nullness and migration

Sting uses [JSpecify](https://jspecify.dev/) for library APIs and generated contracts. Add
`org.jspecify:jspecify:1.0.0` to the compilation classpath, mark your packages or types with `@NullMarked`,
and annotate nullable types with `org.jspecify.annotations.Nullable`. Within a marked scope, other reference
types are nonnull by default.

Replace legacy nullability imports with JSpecify. Remove redundant nonnull annotations when adding a marked
scope, and preserve the intended type-use position of each nullable annotation. Sting rejects legacy nullability
annotations on DI, factory and interceptor declarations with a migration diagnostic.

`@Nullable T` is an optional instance request. Existing `Optional<T>`, `Supplier<Optional<T>>` and collection
forms retain their behavior. Nullable type arguments such as `Supplier<@Nullable T>` are unsupported DI requests;
use a supported optional form instead. Nested nullable types in ordinary intercepted service signatures are preserved.
Array annotations also retain their position: `@Nullable String[]` permits null entries, while
`String @Nullable []` permits a null array.

Generated types are `@NullMarked` and their bodies are checked with NullAway's JSpecify mode. Unmarked consumer
code remains supported through NullAway's optimistic boundary assumptions: its implementation bodies and unspecified
contracts are trusted. Annotate and check consumer code to extend the guarantee to those implementations.
