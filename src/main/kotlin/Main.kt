package jonasmayer.birdview

import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.jgrapht.alg.scoring.*
import org.jgrapht.graph.DefaultDirectedGraph
import java.io.File
import java.awt.Desktop


const val testSrcDir = "src/test/resources/exampleSrc/"
const val chessRepertoireDir = "/home/jonas/AndroidStudioProjects/ChessRepertoire/app/src/main/java"
const val bluetoothChatDir = "/home/jonas/AndroidStudioProjects/BluetoothChat/Application/src/main/java/"

val OUTPUT_DIR = "${System.getProperty("user.home")}/.BirdsView/"
val resFiles = arrayOf("d3v4-brush-lite.js", "d3v4-selectable-force-directed-graph.js",
    "d3v4-selectable-zoomable-force-directed-graph.css", "index.html")
const val GRAPH_JSON = "graph.js"
const val INDEX_HTML = "index.html"

// params
const val FILTER_LOOPS = true
const val EDGES_POINT_TO_CALLED_TYPE = false

fun main(args: Array<String>) {

    setupOutputDir(OUTPUT_DIR)

    if(args.isEmpty()) {
        println("Please specify a source code directory.")
        return
    }
    val srcDir = args[0]

    initParserAndSymbolSolver(srcDir)

    val srcFiles = findJavaFilesInDir(srcDir)
    val compilationUnits = srcFiles.map { StaticJavaParser.parse(it) }

    val graph = DefaultDirectedGraph<String, TypeEdge>(TypeEdge::class.java)

    compilationUnits.forEach { compilationUnit ->
        compilationUnit.findAll(ClassOrInterfaceDeclaration::class.java).forEach { classOrInterfaceDeclaration ->
            try {
                graph.addVertex(classOrInterfaceDeclaration.resolve().qualifiedName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    println("Number of resolved classes and interfaces: ${graph.vertexSet().size}")


    compilationUnits.forEach { compilationUnit ->

            compilationUnit.findAll(MethodCallExpr::class.java).forEach { methodCallExpr ->
                try {
                    val callingType = methodCallExpr.getEnclosingType().resolve().qualifiedName
                    val calledType = methodCallExpr.resolve().declaringType().qualifiedName

                    val skipEdge = !graph.containsVertex(calledType) // Called type is not part of sources
                            || FILTER_LOOPS && callingType == calledType
                    if(!skipEdge) {
                        processNewMethodInvocation(graph, callingType, calledType, EDGES_POINT_TO_CALLED_TYPE)
                    }
                } catch (e: Exception) {
                    /*
                    if(e is UnsolvedSymbolException) {
                        println("Unsolved Symbol: ${e.name}")
                    }
                    */
                }
            }
        }

    // scale for PageRank / Reverse PageRank: 1000
    // scale for AlphaCentrality: 15
    // scale for BetweennessCentrality: 15
    // scale for ClosenessCentrality: 10
    // scale for HarmonicCentrality: 400
    // scale for ClusteringCoefficient: 400
    // scale for Coreness: 5

    val scoringAlgorithm = PageRank(graph)

    generateGraphJson(graph, scoringAlgorithm, 1000, File(OUTPUT_DIR, GRAPH_JSON))
    openVisualizationInBrowser(OUTPUT_DIR + INDEX_HTML)
}

fun openVisualizationInBrowser(url: String) {
    val htmlFile = File(url)
    Desktop.getDesktop().browse(htmlFile.toURI())
}

fun processNewMethodInvocation(graph: DefaultDirectedGraph<String, TypeEdge>, callingType: String, calledType: String,
                               pointEdgeToCalledType: Boolean) {
    val (from, to) = if(pointEdgeToCalledType) arrayOf(callingType, calledType) else arrayOf(calledType, callingType)
    if(!graph.addEdge(from, to, TypeEdge())) {
        graph.getEdge(from, to).methodInvocations++
    }
}


fun initParserAndSymbolSolver(srcDir: String) {
    val typeSolver = CombinedTypeSolver()
    typeSolver.add(ReflectionTypeSolver())
    typeSolver.add(JavaParserTypeSolver(srcDir, ParserConfiguration()))

    val symbolSolver = JavaSymbolSolver(typeSolver)

    StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)
}