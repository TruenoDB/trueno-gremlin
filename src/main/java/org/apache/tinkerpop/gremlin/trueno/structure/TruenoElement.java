package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedElement;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.trueno.driver.lib.core.data_structures.Component;
import org.json.JSONObject;

import java.util.Iterator;

import static org.apache.tinkerpop.gremlin.structure.Column.keys;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class TruenoElement implements Element, WrappedElement<Component> {

    protected final Component baseElement;
    protected final TruenoGraph graph;

    TruenoElement(final Component baseElement, final TruenoGraph graph) {
        /* Sanity checks */
        if (baseElement == null)
            throw  new IllegalArgumentException("Component must not be nul!");

        this.baseElement = baseElement;
        this.graph = graph;
    }

    @Override
    public Object id() {
        return this.baseElement.getId();
    }

    @Override
    public String label() {
        /* Element label is stored on Trueno as a property */
        return this.baseElement.getProperty("label").toString();
    }

    @Override
    public Graph graph() {
        return this.graph;
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        /* Sanity checks */
        ElementHelper.validateProperty(key, value);
        /* Set property to Trueno base element */
        this.getBaseElement().setProperty(key, value);
        return new TruenoProperty<V>(this, key, value);
    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(String... keys) {
        return (Iterator)IteratorUtils.stream(this.getBaseElement().properties().keys())
                .filter(key -> ElementHelper.keyExists(key, keys))
                .map(key -> new TruenoProperty<>(this, key, (V) this.getBaseElement().getProperty(key))).iterator();
    }

    @Override
    public Component getBaseElement() {
        return this.baseElement;
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @Override
    public final boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }


}
