/*
 * Copyright (c) 2018 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package motif.errormessage

import motif.models.InvalidScopeMethod

internal class InvalidScopeMethodHandler(private val error: InvalidScopeMethod) : ErrorHandler {

    override val name = "INVALID SCOPE METHOD"

    override fun StringBuilder.handle() {
        appendln("""
            Scope method is invalid:

              ${error.scope.qualifiedName}.${error.method.name}
        """.trimIndent())
    }
}