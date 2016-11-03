package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedEdge;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.json.JSONObject;

import org.trueno.driver.lib.core.data_structures.Component;

import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoEdge extends TruenoElement implements Edge, WrappedEdge<org.trueno.driver.lib.core.data_structures.Edge> {

    TruenoEdge(final Component baseElement, final TruenoGraph graph) {
        super(baseElement, graph);
    }

    public TruenoEdge(JSONObject object, TruenoGraph graph) {
        super(object, graph);
//        System.out.println("TruenoEdge() --> " + object);
        /* Setting specific edges properties */
        this.getBaseElement().put("target", object.get("target"));
        this.getBaseElement().put("source", object.get("source"));
    }

    @Override
    // FIXME: Actually this is not the best way, since he are returning a vertex with just the id.
    public Vertex outVertex() {
        return new TruenoVertex(TruenoHelper.edgeStart(this), this.graph);
    }

    @Override
    // FIXME: Actually this is not the best way, since he are returning a vertex with just the id.
    public Vertex inVertex() {
        return new TruenoVertex(TruenoHelper.edgeEnd(this), this.graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {

        switch (direction) {
            case OUT:
                return IteratorUtils.of(this.outVertex());
            case IN:
                return IteratorUtils.of(this.inVertex());
            default:
                return IteratorUtils.of(this.outVertex(), this.inVertex());
        }
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

//    @Override
//    public Object id() {
//        return null;
//    }

//    @Override
//    public String label() {
//        return null;
//    }

//    @Override
//    public Graph graph() {
//        return null;
//    }

//    @Override
//    public <V> Property<V> property(String key, V value) {
//        return this.property(key, value);
//    }

    @Override
    public void remove() {
        this.getBaseElement().destroy();
        TruenoHelper.destroy(this);
    }

//    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        Iterator<? extends Property> properties = super.properties(propertyKeys);

        return TruenoHelper.asStream(properties).map(p -> (Property<V>)p).iterator();
    }

    // FIXME: instance a new Edge it's not the base practice, instead a casting should be used
    // (but that will require to use interface and not inheritance)
    @Override
    public org.trueno.driver.lib.core.data_structures.Edge getBaseEdge() {
        return new org.trueno.driver.lib.core.data_structures.Edge (this.getBaseElement(), this.graph.getBaseGraph());
//        return (org.trueno.driver.lib.core.data_structures.Edge)this.getBaseElement();
    }
}
