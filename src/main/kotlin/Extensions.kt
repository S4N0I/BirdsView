package jonasmayer.birdview

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm
import org.jgrapht.alg.scoring.*

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

fun VertexScoringAlgorithm<String, Double>.getDefaultScoreScale(): Int = when(this) {
    is PageRank<*,*> -> 1000
    is BetweennessCentrality<*,*> -> 15
    is ClosenessCentrality<*,*> -> 10
    is HarmonicCentrality<*,*> -> 400
    is ClusteringCoefficient<*,*> -> 400
    is Coreness<*,*> -> 5
    else -> 1
}