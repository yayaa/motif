package motif.intellij

import com.intellij.openapi.project.Project
import com.jakewharton.rxrelay2.PublishRelay
import motif.ir.SourceSetGenerator
import motif.models.graph.Graph
import motif.models.graph.GraphFactory
import motif.models.graph.errors.GraphValidationErrors
import java.util.concurrent.TimeUnit

class Validation {

    private val validationEvents: PublishRelay<Project> = PublishRelay.create()
    private var graph: Graph = Graph(mapOf(), null)

    init {
        validationEvents
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    println("Regenerating Graph")
                    val scopes = SourceSetGenerator().createSourceSet(it)
                    graph = GraphFactory.create(scopes)
                }
    }

    fun getValidationErrors(project: Project): GraphValidationErrors {
        println("Requesting Validation Errors")
        validationEvents.accept(project)
        return graph.validationErrors
    }
}