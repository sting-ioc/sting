---
title: Kinds of Dependencies
---

Sting allows a component to declare different kinds of dependencies. The kind determines the manner in which
Sting supplies the dependency to the component. The kind is specified by the java type of the constructor
parameter (for {@link: sting.Injectable} annotated types), the method parameter (for methods contained in
{@link: sting.Fragment} annotated types) or the method return value (for injector [outputs](outputs.md)).
The kind can also be determined by whether the element has been annotated by the `@javax.annotation.Nullable`
annotation.

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
    <td>Collection</td>
    <td nowrap><code>Collection&lt;T&gt;</code></td>
    <td>
      Sting provides a read-only collection containing every component that publishes
      the type <code>T</code>. This is primarily aimed at systems that support multiple
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
