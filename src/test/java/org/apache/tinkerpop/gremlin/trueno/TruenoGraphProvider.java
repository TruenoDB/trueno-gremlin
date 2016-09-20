package org.apache.tinkerpop.gremlin.trueno;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.AbstractGraphProvider;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.Map;
import java.util.Set;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoGraphProvider extends AbstractGraphProvider {

    @Override
    public Map<String, Object> getBaseConfiguration(String s, Class<?> aClass, String s1, LoadGraphWith.GraphData graphData) {
        return null;
    }

    @Override
    public void clear(Graph graph, Configuration configuration) throws Exception {

    }

    @Override
    public Set<Class> getImplementations() {
        return null;
    }
}
