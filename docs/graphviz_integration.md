---
title: GraphViz Integration
---

Sometimes understanding the component graph within your application is difficult so Sting can generate
a [.dot](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) report to visualize the component
graph. The report can be passed into [GraphViz](https://en.wikipedia.org/wiki/Graphviz) or any other tool
that supports the [.dot](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) graph description
language.

To generate the report it is necessary to set the annotation processor option `sting.emit_dot_reports` to
the value `true` which is typically done by passing the command line option `-Asting.emit_dot_reports=true`
to the javac compiler. This will generate a file with the same name as the injector and the filename
extension `.gv`. So the class `com.biz.MyInjector` will produce a report named `com/biz/MyInjector.gv`.

This report can be converted into images using any number of different tools but we often use a tool such
as [`neato`](https://en.wikipedia.org/wiki/Graphviz) to generate svg images using a command such as:

        > neato  -Tsvg -o com/biz/MyInjector.svg com/biz/MyInjector.gv

An example of the type of images produced from a .dot report.

<figure>
    <img src="/img/TodoInjector.svg" style="width:100%" alt="Image of Injector in the react4j-todomvc project">
    <figcaption>An example image of Injector in the <a href="https://github.com/react4j/react4j-todomvc/tree/sting">react4j-todomvc</a> project</figcaption>
</figure>

The nodes are colored blue if they are eager while the injector is colored green. The connections between the
nodes have the symbology as follows:

<img src="/img/legend.svg" style="width:100%" alt="Connector Legend">
