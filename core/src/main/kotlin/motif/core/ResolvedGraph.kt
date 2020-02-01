/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
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
package motif.core

import motif.ast.IrClass
import motif.ast.IrType
import motif.models.*

sealed class ResolvedGraph(val errors: List<MotifError>, val scopes: List<Scope>) {
    abstract fun getScope(scopeType: IrType): Scope?

    companion object {
        fun create(initialScopeClasses: List<IrClass>): ResolvedGraph {
            val scopes = Scope.fromClasses(initialScopeClasses)
            val scopeGraph = ScopeGraph.create(scopes)
            scopeGraph.scopeCycleError?.let { return CyclicGraph(it) }
            return ResolvedGraphFactory(scopeGraph).create()
        }
    }
}

private class ResolvedGraphFactory(private val scopeGraph: ScopeGraph) {

    private val scopeStates = mutableMapOf<Scope, State>()
    private val childStates = mutableMapOf<ScopeEdge, State>()

    fun create(): ResolvedGraph {
        val states = scopeGraph.roots.map { getState(it) }
        val state = State.merge(states)
        return ValidResolvedGraph(scopeGraph, scopeStates, childStates, state)
    }

    private fun getState(scope: Scope): State {
        // Not using computeIfAbsent here since it seems to have undefined behavior when modifying
        // the map inside the lambda.
        return scopeStates[scope] ?: createState(scope).apply { scopeStates[scope] = this }
    }

    private fun createState(scope: Scope): State {
        val childStates = scopeGraph.getChildEdges(scope).map { childEdge ->
            val childState = getState(childEdge.child).copy()
            childState.addSources(childEdge.method.sources)
            childState.requireExpose()
            childStates[childEdge] = childState
            childState
        }
        val state = State.merge(childStates)

        val factoryMethodSinks = scope.factoryMethods.flatMap { it.sinks }
        state.addSinks(factoryMethodSinks)

        val accessMethodSinks = scope.accessMethods.map { it.sink }
        state.addSinks(accessMethodSinks)

        val factoryMethodSources = scope.factoryMethods.flatMap { it.sources }
        state.addSources(factoryMethodSources)

        val scopeSource = scope.source
        state.addSource(scopeSource)

        scope.dependencies?.let { state.setDependencies(it) }

        scope.factoryMethods.forEach { factoryMethod ->
            factoryMethod.sources.forEach { source ->
                state.addEdges(source, factoryMethod.sinks)
            }
        }

        state.checkCycle()

        return state
    }
}

private class CyclicGraph(error: MotifError) : ResolvedGraph(listOf(error), emptyList()) {
    override fun getScope(scopeType: IrType): Scope? = null
}

private class ValidResolvedGraph(
        private val scopeGraph: ScopeGraph,
        private val scopeStates: Map<Scope, State>,
        private val childStates: Map<ScopeEdge, State>,
        private val graphState: State
) : ResolvedGraph(scopeGraph.parsingErrors + graphState.errors, scopeGraph.scopes) {

    private val scopeSinks = mutableMapOf<Scope, Set<Sink>>()

    val roots = scopeGraph.roots

    override fun getScope(scopeType: IrType) = scopeGraph.getScope(scopeType)

    fun getChildEdges(scope: Scope) = scopeGraph.getChildEdges(scope)

    fun getParentEdges(scope: Scope) = scopeGraph.getParentEdges(scope)

    fun getChildUnsatisfied(scopeEdge: ScopeEdge) = childStates.getValue(scopeEdge).unsatisfied

    fun getUnsatisfied(scope: Scope) = scopeStates.getValue(scope).unsatisfied.groupBy { it.type }

    fun getSources(scope: Scope) = scopeStates.getValue(scope).sourceToSinks.keys.filter { it.scope == scope }

    fun getSinks(type: Type) = graphState.sinks[type] ?: emptySet<Sink>()

    fun getSinks(irType: IrType) = graphState.irTypeToSinks[irType] ?: emptySet<Sink>()

    fun getSources(irType: IrType) = graphState.irTypeToSources[irType] ?: emptySet<Source>()

    fun getSinks(scope: Scope) = scopeSinks.computeIfAbsent(scope) {
        scopeStates.getValue(scope).sinks.values.flatMap { sinks ->
            sinks.filter { sink -> sink.scope == scope }
        }.toSet()
    }

    fun getProviders(sink: Sink) = graphState.sinkToSources.getValue(sink)

    fun getConsumers(source: Source) = graphState.sourceToSinks.getValue(source)

    fun getRequired(source: Source) = scopeStates.getValue(source.scope).edges[source] ?: emptyList()
}
