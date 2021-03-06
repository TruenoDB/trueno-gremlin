package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedElement;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.codehaus.groovy.tools.StringHelper;
import org.trueno.driver.lib.core.data_structures.Component;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class TruenoElement implements Element, WrappedElement<Component> {
    protected final TruenoGraph graph;
    protected final Component baseElement;

    TruenoElement(final Component baseElement, final TruenoGraph graph) {
        /* Sanity checks */
        if (baseElement == null)
            throw  new IllegalArgumentException("Component must not be nul!");

        this.baseElement = baseElement;
        this.graph = graph;
    }

    TruenoElement(final JSONObject obj, final TruenoGraph graph) {
        /* Sanity checks */
        if (obj == null)
            throw  new IllegalArgumentException("Component must not be nul!");

//        System.out.println("TruenoElement() --> " + obj);
        Component baseElement = new Component(obj);
//        System.out.println("TruenoElement() --> " + obj);

        this.baseElement = baseElement;
        this.graph = graph;
    }

    @Override
    public Object id() {
        return this.baseElement.getId();
    }

    @Override
    public String label() {
        return this.baseElement.getLabel();
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
    public <V> Iterator<? extends Property<V>> properties(String... propertyKeys) {
        return (Iterator)IteratorUtils.stream(this.getBaseElement().properties().keys())
                .filter(key -> ElementHelper.keyExists(key, propertyKeys))
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

    @Override
    public String toString() {
        return this.baseElement.toString();
    }

}
