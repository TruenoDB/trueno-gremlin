package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedEdge;
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
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        return null;
    }

    @Override
    public Object id() {
        return null;
    }

//    @Override
//    public String label() {
//        return null;
//    }

//    @Override
//    public Graph graph() {
//        return null;
//    }

    // TODO: Probably will be convenient to filter 'label' property
    @Override
    public <V> Property<V> property(String key, V value) {
        return this.property(key, value);
    }

    @Override
    public void remove() {
        this.getBaseElement().destroy();
        TruenoHelper.destroy(this);
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        // TODO: Implement (should be similar for Edge)
        return null;
    }

    @Override
    public org.trueno.driver.lib.core.data_structures.Edge getBaseEdge() {
        return (org.trueno.driver.lib.core.data_structures.Edge)this.getBaseElement();
    }
}
