---
title: Auto-Fragments
---

The {@link: sting.AutoFragment @AutoFragment} annotation is used to generate [fragments](fragments.md) by collecting all
types annotated with a matching {@link: sting.ContributeTo @ContributeTo} annotation and generating a fragment. The
auto-fragments capability make managing applications with large numbers of [non-auto-discoverable](autodiscovery.md)
components much much easier.

Prior to Auto-Fragments, the addition of a component often required that a fragment or injector be modified to
include the new component in the includes list. If different components were added in different branches, it was
possible to cause conflicts when the different branches were merged. These conflicts would require that a developer
spend time resolving the conflicts.

Auto-fragments allow the developer to define a {@link: sting.AutoFragment @AutoFragment} annotated interface such as:

{@file_content: file=sting/doc/examples/autofragments/EntitiesAutoFragment.java start_line=@AutoFragment}
 
And then define components separately and ensure that they are annotated with the
{@link: sting.ContributeTo @ContributeTo} with a value matching the {@link: sting.AutoFragment#value() @AutoFragment.value}
parameter.

For example a developer could define the following classes:

{@file_content: file=sting/doc/examples/autofragments/UserRepository.java start_line=@Injectable}

{@file_content: file=sting/doc/examples/autofragments/GroupRepository.java start_line=@Injectable}

{@file_content: file=sting/doc/examples/autofragments/MessageRepositoryFragment.java start_line=@Fragment}

and the annotation processor would generate the fragment

{@file_content: path=generated/processors/main/java file=sting/doc/examples/autofragments/Sting_EntitiesAutoFragment_Fragment.java start_line=@Fragment}

As you could imagine, auto-fragments significantly reduce the amount of potentially conflict causing boilerplate
code that is added or removed when a component is added or removed from the application. However, auto-fragments
are not without significant constraints and limitations.

The primary limitation is that the types annotated with the {@link: sting.AutoFragment @AutoFragment} annotation
and the the types annotated with the {@link: sting.ContributeTo @ContributeTo} annotation with the same value
**MUST** be compiled in the same javac invocation. If they are not compiled by the same javac invocation, then
the annotation processor will be unable to match the types and will generate an error. Other limitations are
outlined in the {@link: sting.AutoFragment @AutoFragment} javadocs.

These limitations do not impact builds systems such as [Bazel](https://bazel.build/) that explicitly control
inputs and outputs for build tasks. Other build systems that support incremental builds may not be suitable
for use with auto-fragments. This is part of the reason why auto-fragments are considered an advanced technique
and may evolve in the future.
