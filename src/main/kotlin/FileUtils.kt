package jonasmayer.birdview

import org.jgrapht.alg.interfaces.VertexScoringAlgorithm
import org.jgrapht.graph.DefaultDirectedGraph
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.streams.toList


fun findJavaFilesInDir(dir: String): List<File> {
    return Files.walk(Paths.get(dir))
        .filter { Files.isRegularFile(it) && it.toString().endsWith(".java") }
        .map { it.toFile() }
        .toList()
}

fun setupOutputDir(outputDir: String) {
    if(File(outputDir).mkdirs()) {
        resFiles.forEach {
            val inputStream = object {}.javaClass.getResourceAsStream("/$it")
            Files.copy(inputStream, Paths.get(outputDir + it), StandardCopyOption.REPLACE_EXISTING) }
    }
}

fun generateGraphJson(graph: DefaultDirectedGraph<String, TypeEdge>, scoringAlgorithm: VertexScoringAlgorithm<String, Double>,
                      scoreScale: Int, outputFile: File) {
    if(outputFile.exists()) {
        outputFile.delete()
    }
    outputFile.createNewFile()
    outputFile.outputStream().use {
        val bufferedWriter = it.bufferedWriter(Charsets.UTF_8)
        bufferedWriter.write("""
            var graph = { "nodes": [
        """.trimIndent())

        // write all nodes
        graph.vertexSet().forEachIndexed { index, node ->
            bufferedWriter.write("""
                ${if(index == 0) "" else ","}{"id":"$node","score":${(scoringAlgorithm.getVertexScore(node)*scoreScale).toInt()}}
            """.trimIndent())
        }

        bufferedWriter.write("""
            ], "links": [
        """.trimIndent())

        // write all edges
        graph.edgeSet().forEachIndexed { index, edge ->
            bufferedWriter.write("""
                ${if(index == 0) "" else ","}{"source":"${edge.source()}","target":"${edge.target()}","value":${edge.methodInvocations}}
            """.trimIndent())
        }
        bufferedWriter.write("]};")
        bufferedWriter.flush()
    }
}