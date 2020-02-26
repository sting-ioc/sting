---
title: Overview
---

Sting models the application as a set of components. An injector is responsible for constructing
and linking components together. Components are linked to other components by services. A service
is just a java type with an optional string qualifier. A component can consume services published
by other components and/or publish services for other components to consume.

Sting performs builds a directed graph of components at compile time, linking components via services.
Unless a service is marked as optional then there must exist a component that publishes a service
if another component requires the service. The compiler analyzes the graph to ensure that is well
formed and contains no cycles. If the analysis is successful then Sting will generate java source
code to implement the injector.

Sting can create components directly if they are annotated with the {@api_url: Injectable} annotation
or it can call out to user code to provide the component (i.e. methods in {@api_url: Fragment} annotated
types.) The second form is used when third-party objects can't be annotated, when a different framework is
responsible for creating the object (i.e `GWT.create(MyGwtRpcService.class)` or when it is awkward to
create the type (i.e. when publishing a component that is provided by calling a method on another
component).  
