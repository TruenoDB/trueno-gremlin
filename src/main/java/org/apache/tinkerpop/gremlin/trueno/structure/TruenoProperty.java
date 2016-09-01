package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.NoSuchElementException;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoProperty implements Property {

    @Override
    public String key() {
        return null;
    }

    @Override
    public Object value() throws NoSuchElementException {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Element element() {
        return null;
    }

    @Override
    public void remove() {

    }

}
