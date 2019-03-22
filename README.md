## About BirdsView
**BirdsView is a tool to visualize the structure of Java projects.**

It takes source code and builds a graph of classes and interaces based on method invocations.
Vertices are assigned a score according to PageRank or another algorithm of choice.
The result is visualized in a force-directed graph.

**What you get is a visualization of your project structure:**

Tightly coupled classes end up close to each other while unrelated classes don't.
Classes which encapsulate a lot of control (they call many other classes' methods) appear as larger nodes.

On top of that, **you can hack the generated graph visualization** with an embedded JS editor.


## Installation and Usage (Unix)
prerequisites: Java 8+, a web browser

- **Step 1:** Download the jar
- **Step 2:** Run the jar:
```bash
java -jar BirdsView-1.0.jar PATH_TO_YOUR_SRC
```
**Important:**
The PATH_TO_YOUR_SRC has to reference the parent of your java package structure.
Meaning if your directory structure is something like:
```bash
/home/you/IdeaProjects/YourProject/src/main/java/com/yourdomain/yourpackage/YourClass.java
```

You would pass `/home/you/IdeaProjects/YourProject/src/main/java/` for PATH_TO_YOUR_SRC.

## Optional Args
- **-h** for help
- **--scoreScale FACTOR** to provide an _integer_ factor that is applied to vertex scores. If not specified, a default factor specific to the scoring algorithm is used.
- The algorithm for vertex scoring:

**--pageRank** for PageRank (default)
**--alpha** for AlphaCentrality
**--betweenness** for BetweennessCentrality
**--closeness** for ClosenessCentrality
**--harmonic** for HarmonicCentralilty
**--clustering** for ClusteringCoefficient
- **--reverse** to point edges from the calling type to the called type

## References
This project uses...

- [JavaParser](https://github.com/javaparser/javaparser) and [JavaSymbolSolver](https://github.com/javaparser/javasymbolsolver) for source code analysis
- [JGraphT](https://github.com/jgrapht/jgrapht) for graph representation and algorithms
- [kotlin-argparser](https://github.com/xenomachina/kotlin-argparser) for easy arg parsing
- [Ace](https://github.com/ajaxorg/ace) for the embedded js editor
- [D3](https://github.com/d3/d3d3js) for graph visualization
- [Kotlin](https://github.com/JetBrains/kotlin) because it's the best

[This gist](https://gist.github.com/pkerpedjiev/f2e6ebb2532dae603de13f0606563f5b) created by Peter Kerpedjiev is the base of the visualization code.


