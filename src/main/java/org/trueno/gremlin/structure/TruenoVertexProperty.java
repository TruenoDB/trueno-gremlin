package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import org.trueno.driver.lib.core.data_structures.Component;

import java.util.Iterator;

/**
 * @author @author Edgardo Barsallo Yi (ebarsallo)
 */
public final class TruenoVertexProperty<V> extends TruenoProperty<V> implements VertexProperty<V> {

    TruenoVertexProperty(final TruenoVertex vertex, final String key, final V value) {
        super(vertex, key, value);
    }

    @Override
    public Object id() {
        // FIXME. Using a similar technique than Neo4J plugin. Need for a better identifier.
        return (long) (this.key.hashCode() + this.value.hashCode() + this.id().hashCode());
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        ElementHelper.validateProperty(key, value);
        return this.element.property(key, value);
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... keys) {
        Component elem = ((TruenoVertex)this.element()).getBaseElement();

        return (Iterator) IteratorUtils.stream(elem.properties().keys())
                .filter(key -> ElementHelper.keyExists(key, keys))
                .map(key -> new TruenoProperty<>(this, key, (V) elem.getProperty(key))).iterator();
    }

    @Override
    public Vertex element() {
        return (TruenoVertex) this.element;
    }
}
