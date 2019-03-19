package jonasmayer.birdview

import org.jgrapht.Graph
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm
import org.jgrapht.alg.scoring.*

enum class VertexScoringAlgorithmOption {
    PAGE_RANK {
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= PageRank(graph)
    },
    ALPHA_CENTRALITY {
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= AlphaCentrality(graph)
    },
    BETWEENNESS_CENTRALITY{
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= BetweennessCentrality(graph)
    },
    CLOSENESS_CENTRALITY{
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= ClosenessCentrality(graph)
    },
    HARMONIC_CENTRALITY{
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= HarmonicCentrality(graph)
    },
    CLUSTERING_COEFFICIENT{
        override fun getAlgorithm(graph: Graph<String, TypeEdge>)= ClusteringCoefficient(graph)
    };

    abstract fun getAlgorithm(graph: Graph<String,TypeEdge>): VertexScoringAlgorithm<String, Double>
}