package jonasmayer.birdview

import org.jgrapht.graph.DefaultEdge

data class TypeEdge(var methodInvocations: Int = 1): DefaultEdge() {

    override fun equals(other: Any?): Boolean {
        return other != null && other is TypeEdge && other.source == this.source && other.target == this.target
    }

    fun source(): String = source as String
    fun target(): String = target as String
}