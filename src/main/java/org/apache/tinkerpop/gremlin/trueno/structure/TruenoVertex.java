package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedVertex;

import org.trueno.driver.lib.core.data_structures.*;

import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoVertex extends TruenoElement implements Vertex, WrappedVertex<org.trueno.driver.lib.core.data_structures.Vertex> {

    TruenoVertex(final Component baseElement, final TruenoGraph graph) {
        super(baseElement, graph);
    }

    @Override
    public Edge addEdge(String s, Vertex vertex, Object... objects) {
        // TODO: public Edge addEdge(...)
        return null;
    }

    @Override
    public <V> VertexProperty<V> property(final String key, final V value) {
        return this.property(key, value);
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        ElementHelper.validateProperty(key, value);
        /* Only single cardinality supported for now */
        if (cardinality != VertexProperty.Cardinality.single)
            throw VertexProperty.Exceptions.multiPropertiesNotSupported();
        try {
            this.getBaseElement().setProperty(key, value);
            return new TruenoVertexProperty<>(this, key, value);
        } catch (final IllegalArgumentException iae) {
            throw Property.Exceptions.dataTypeOfPropertyValueNotSupported(value, iae);
        }
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        // TODO: public Iterator<Edge> edges(...)
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        return (Iterator)TruenoHelper.getVertices(this, direction, edgeLabels);
    }

//    @Override
//    public Object id() {
//        return null;
//    }

//    @Override
//    public String label() {
//        return this.getBaseVertex().getProperty("label");
//    }

    @Override
    public void remove() {
        this.getBaseElement().destroy();
        this.graph.getBaseGraph().destroy().whenComplete((ret, error) -> {
            if (ret != null) {

            } else {
                throw new Error ("Something bad happened: " + error);
            }
        });
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... strings) {
        return null;
    }

    @Override
    public org.trueno.driver.lib.core.data_structures.Vertex getBaseVertex() {
        return (org.trueno.driver.lib.core.data_structures.Vertex)this.getBaseElement();
    }
}
