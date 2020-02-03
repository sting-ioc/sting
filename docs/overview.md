---
title: Overview
---

Sting models the application as a set of components. The application is defined by an injector
which is responsible for constructing and linking components together. Components are linked to
other components by services. A component can consume services and/or publish services for other
components to consume.

Sting performs builds a directed graph of components at compile time, linking components via services
so that there is a component that publishes a service if another component requires a service. The
compiler analyzes the graph to ensure that is well formed and contains no cycles. If analysis is
successful then Sting will generate java source code to implement the injector.

Sting can create components directly if they are annotated with the {@api_url: Injectable} annotation
or it can call out to user code to construct the component (i.e. {@api_url: Provides} annotated methods
in {@api_url: Fragment} annotated types.)
