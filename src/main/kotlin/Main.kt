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
import java.io.File


val javaTestCodeDir = "src/test/resources/exampleSrc"
val srcFiles = listOf("A.java", "B.java")

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

    val typeSolver = CombinedTypeSolver()
    typeSolver.add(ReflectionTypeSolver())
    typeSolver.add(JavaParserTypeSolver(javaTestCodeDir, ParserConfiguration()))

    val symbolSolver = JavaSymbolSolver(typeSolver)

    StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)

    srcFiles.stream()
        .map { StaticJavaParser.parse(File(javaTestCodeDir, it)) }
        .forEach { compilationUnit ->
            compilationUnit.findAll(MethodCallExpr::class.java).forEach { methodCallExpr ->
                println("Method Call Expression: $methodCallExpr")
                println("calling type: ${methodCallExpr.getEnclosingType().resolve().qualifiedName}")
                println("called type: ${methodCallExpr.resolve().declaringType().qualifiedName}\n")
            }
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