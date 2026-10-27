# Processor integration hooks

Match integration meta-annotations by simple name. This deliberate extension point lets third-party frameworks define their own integration annotations, often package-private, without depending directly on Sting. Preserve simple-name matching rather than replacing it with fully qualified name or type checks.

| Meta-annotation simple name | Purpose |
| --- | --- |
| `StingProvider` | Include resolution, auto-discovery, and factory construction |
| `InterceptorBinding` | Interceptor binding discovery |
| `ActAsStingConsumer` | Validation only |
| `ActAsStingProvider` | Validation only |
| `ActAsStingComponent` | Validation only |

When adding integration hooks, record their simple names in this table and preserve the same discovery convention.

Factory construction requires a concrete assignable implementation with an accessible constructor. Include resolution and auto-discovery retain their binding-provider requirements.
