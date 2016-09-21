package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;

import org.trueno.driver.lib.core.data_structures.Component;

import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoElement implements Element {

    protected final Component baseElement;
    protected final TruenoGraph graph;

    public TruenoElement(final Component baseElement, final TruenoGraph graph) {
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
    public <V> Property<V> property(String s, V v) {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(String... strings) {
        return null;
    }

}
