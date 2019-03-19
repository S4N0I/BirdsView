package jonasmayer.birdview

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser: ArgParser) {

    val vertexScoringAlgorithm by parser.mapping<VertexScoringAlgorithmOption>(
        "--pageRank" to VertexScoringAlgorithmOption.PAGE_RANK,
        "--alpha" to VertexScoringAlgorithmOption.ALPHA_CENTRALITY,
        "--betweenness" to VertexScoringAlgorithmOption.BETWEENNESS_CENTRALITY,
        "--closeness" to VertexScoringAlgorithmOption.CLOSENESS_CENTRALITY,
        "--harmonic" to VertexScoringAlgorithmOption.HARMONIC_CENTRALITY,
        "--clustering" to VertexScoringAlgorithmOption.CLUSTERING_COEFFICIENT,
        help = """
            the algorithm for vertex scoring

            --pageRank for PageRank
            --alpha for AlphaCentrality
            --betweenness for BetweennessCentrality
            --closeness for ClosenessCentrality
            --harmonic for HarmonicCentralilty
            --clustering for ClusteringCoefficient

            default: PageRank
        """.trimIndent()
    ).default(VertexScoringAlgorithmOption.PAGE_RANK)

    val vertexScoreScale by parser.storing("--scoreScale", help = """
        scale factor for vertex scores (integer)

        If no value is provided, a default value specific to the scoring algorithm is used.
    """.trimIndent()) { toInt() }.default{ null }

    val reverseEdges by parser.flagging("--reverse",
        help = """
            point edges from the calling type to the called type
        """.trimIndent()).default(false)

    val sourceDir by parser.positional("SRC_DIR",
        help = """
            source code directory

            IMPORTANT: this has to be the closest parent to the directories that reflect the Java package structure.
            This means, for example, if your directory structure is like Yourproject/src/main/java/com/yourdomain/yourproject
            you should pass Yourproject/src/main/java as SRC_DIR.
        """.trimIndent())
}