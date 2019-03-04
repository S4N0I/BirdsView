package jonasmayer.birdview

import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.jgrapht.alg.scoring.PageRank
import org.jgrapht.graph.DefaultDirectedGraph
import java.io.File


const val javaTestCodeDir = "src/test/resources/exampleSrc"
val srcFiles = listOf("A.java", "B.java")

// params
const val ONLY_CONSIDER_CALLS_TO_OWN_SRC = true
const val FILTER_LOOPS = true
const val OUTPUT_FILE = "graph.json"

fun main() {

    /*
    This is how this will work:
    - Find all src files in relevant directory
    - Create Java Parser (sources, stdlib)
    - Parse all src files
    - Build the graph
    - Run graph algorithms
    - persist / visualize graph
    */

    initParserAndSymbolSolver(rootSrcDir = javaTestCodeDir)

    val graph = DefaultDirectedGraph<String, TypeEdge>(TypeEdge::class.java)

    srcFiles.map { StaticJavaParser.parse(File(javaTestCodeDir, it)) }
        .forEach { compilationUnit ->
            compilationUnit.findAll(MethodCallExpr::class.java).forEach { methodCallExpr ->
                val callingType = methodCallExpr.getEnclosingType().resolve().qualifiedName
                val calledType = methodCallExpr.resolve().declaringType().qualifiedName

                val skipEdge = ONLY_CONSIDER_CALLS_TO_OWN_SRC && calledType.startsWith("java.")
                            || FILTER_LOOPS && callingType == calledType
                if(!skipEdge) {
                    // processEdge(graph, callingType, calledType)
                    processEdgeReversed(graph, callingType, calledType)
                }
            }
        }

    generateGraphJson(graph, PageRank(graph), File(OUTPUT_FILE))
}

fun processEdgeReversed(graph: DefaultDirectedGraph<String, TypeEdge>, callingType: String, calledType: String) {
    processEdge(graph, calledType, callingType)
}

fun processEdge(graph: DefaultDirectedGraph<String, TypeEdge>, callingType: String, calledType: String) {
    graph.addVertex(callingType)
    graph.addVertex(calledType)
    if(!graph.addEdge(callingType, calledType, TypeEdge())) {
        graph.getEdge(callingType, calledType).count++
    }
}

fun generateGraphJson(graph: DefaultDirectedGraph<String, TypeEdge>, pageRank: PageRank<String, TypeEdge>, outputFile: File) {
    if(outputFile.exists()) {
        outputFile.delete()
    }
    outputFile.createNewFile()
    outputFile.outputStream().use {
        val bufferedWriter = it.bufferedWriter(Charsets.UTF_8)
        bufferedWriter.write("""
            { "nodes": [
        """.trimIndent())

        // write all nodes
        graph.vertexSet().forEachIndexed { index, node ->
            bufferedWriter.write("""
                ${if(index == 0) "" else ","}{"id":"$node","freq":${(pageRank.getVertexScore(node)*1000).toInt()}}
            """.trimIndent())
        }

        bufferedWriter.write("""
            ], "links": [
        """.trimIndent())

        // write all edges
        graph.edgeSet().forEachIndexed { index, edge ->
            bufferedWriter.write("""
                ${if(index == 0) "" else ","}{"source":"${edge.source()}","target":"${edge.target()}","value":${edge.count}}
            """.trimIndent())
        }
        bufferedWriter.write("]}")
        bufferedWriter.flush()
    }
}

fun MethodCallExpr.getEnclosingType(): ClassOrInterfaceDeclaration {
    var currentNode: Node = this
    while(currentNode.parentNode.isPresent) {
        val parentNode = currentNode.parentNode.get()
        if(parentNode is ClassOrInterfaceDeclaration) {
            return parentNode
        } else {
            currentNode = parentNode
        }
    }
    throw IllegalStateException("MethodCallExpr without enclosing ClassOrInterfaceDeclaration!")
}


fun initParserAndSymbolSolver(rootSrcDir: String) {
    val typeSolver = CombinedTypeSolver()
    typeSolver.add(ReflectionTypeSolver())
    typeSolver.add(JavaParserTypeSolver(rootSrcDir, ParserConfiguration()))

    val symbolSolver = JavaSymbolSolver(typeSolver)

    StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)
}