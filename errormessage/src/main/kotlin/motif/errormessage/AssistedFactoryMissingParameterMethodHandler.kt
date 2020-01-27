package motif.errormessage

import motif.models.AssistedFactoryMissingParameter

class AssistedFactoryMissingParameterMethodHandler(private val error: AssistedFactoryMissingParameter) : ErrorHandler {

    override val name: String = "MISSING PARAMETER FOR ASSISTED FACTORY"

    override fun StringBuilder.handle() {
        appendln("""
            Assisted Factory method ${error.method.name} 
            with parameters ${error.method.parameters} cannot be satisfied, 
            because the following parameters are not provided in the scope ${error.scope.qualifiedName}
            
            Missing parameters: ${error.missingParameters}
        """.trimIndent())
    }

}