package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedVertex;
import org.json.JSONObject;

import org.trueno.driver.lib.core.data_structures.Component;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoVertex extends TruenoElement implements Vertex, WrappedVertex<org.trueno.driver.lib.core.data_structures.Vertex> {

    TruenoVertex(final Component baseElement, final TruenoGraph graph) {
        super(baseElement, graph);
    }

    public TruenoVertex(JSONObject object, TruenoGraph graph) {
        super(object, graph);
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        if (null == inVertex) throw Graph.Exceptions.argumentCanNotBeNull("inVertex");
        ElementHelper.validateLabel(label);
        ElementHelper.legalPropertyKeyValueArray(keyValues);

        // FIXME: edge id should be auto generated, and not provided by the user (as mandatory).
        if (!ElementHelper.getIdValue(keyValues).isPresent()) {
            throw new RuntimeException("Id Not Supplied");
        }

        final org.trueno.driver.lib.core.data_structures.Vertex outVertex = (org.trueno.driver.lib.core.data_structures.Vertex) this.getBaseElement();
        final TruenoEdge edge = new TruenoEdge(this.graph.getBaseGraph().addEdge(outVertex.getId(), ((TruenoVertex)inVertex).getBaseVertex().getId()), this.graph);
        ElementHelper.attachProperties(edge, keyValues);

        // TODO: implement vertex.addEdge(inVertex, label) in java-driver, and use it here.
        edge.getBaseElement().setLabel(label);
        edge.getBaseElement().setId(ElementHelper.getIdValue(keyValues).get());
        TruenoHelper.persist(edge);
        return edge;
    }

    @Override
    public <V> VertexProperty<V> property(final String key, final V value) {
        return this.property(key, value);
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        ElementHelper.validateProperty(key, value);
//        System.out.println(" >> " + key + " ==> " + cardinality);
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
    public <V> VertexProperty<V> property(final String key) {
        return (this.getBaseVertex().hasProperty(key))? new TruenoVertexProperty<V>(this, key, (V)this.getBaseVertex().getProperty(key)): VertexProperty.<V>empty();
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        // FIXME: Handler better this exception.
        try {
            System.out.println("...edges");
            return (Iterator)TruenoHelper.getEdges(this, direction, edgeLabels);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        // FIXME: Handler better this exception.
        try {
            return (Iterator)TruenoHelper.getVertices(this, direction, edgeLabels);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }
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
        TruenoHelper.destroy(this);
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {

        Iterator<? extends Property> properties = super.properties(propertyKeys);

//        System.out.println("properties 1 --> " + properties);
//        VertexProperty<V> prop = (VertexProperty<V>)properties;
//        System.out.println("properties 2 --> " + prop);

        return TruenoHelper.asStream(properties).map(p -> (VertexProperty<V>)new TruenoVertexProperty<V>((TruenoVertex)p.element(), p.key(), (V)p.value() )).iterator();
    }

    @Override
    public org.trueno.driver.lib.core.data_structures.Vertex getBaseVertex() {
        return new org.trueno.driver.lib.core.data_structures.Vertex (this.getBaseElement(), this.graph.getBaseGraph());
//        return (org.trueno.driver.lib.core.data_structures.Vertex)this.getBaseElement();
    }

    // FIXME: There should be an addEdge, connectTo or something similar in Vertex, not in Graph.
}
