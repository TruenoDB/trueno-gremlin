package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphVariableHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.trueno.driver.lib.core.Trueno;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Graph variables are a set of key/value pairs associated with the graph. Variables are stored as properties on
 * base graph structure.
 *
 * @author Edgardo Barsallo Yi
 */
public final class TruenoGraphVariables implements Graph.Variables {

    protected final TruenoGraph graph;
    protected org.trueno.driver.lib.core.data_structures.Graph baseGraph;


    public TruenoGraphVariables(final TruenoGraph graph) {
        this.graph = graph;
        this.baseGraph = graph.getBaseGraph();
    }

    @Override
    public Set<String> keys() {
        final Set<String> map = new HashSet<>();
        final Iterator<String> keys = this.baseGraph.properties().keys();
        // TODO: Implement hidden key checking.
        while (keys.hasNext()) {
            map.add(keys.next());
        }
        return map;
    }

    @Override
    public <R> Optional<R> get(final String key) {
        return this.baseGraph.hasProperty(key) ?
                Optional.of((R) this.baseGraph.getProperty(key)) :
                Optional.<R>empty();
    }

    @Override
    public void set(final String key, final Object value) {
        GraphVariableHelper.validateVariable(key, value);
        // TODO: Implement type checking (eg: dataTypeOfVariableValueNotSupported
        try {
            this.baseGraph.setProperty(key, value);
        } catch (final IllegalArgumentException e) {
            throw Graph.Variables.Exceptions.dataTypeOfVariableValueNotSupported(value, e);
        }

    }

    @Override
    public void remove(String key) {
        if (this.baseGraph.hasProperty(key))
            this.baseGraph.removeProperty(key);
    }

    @Override
    public String toString() {
        return StringFactory.graphVariablesString(this);
    }
}
