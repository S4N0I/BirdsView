package jonasmayer.birdview

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr

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