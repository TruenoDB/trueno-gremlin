package org.apache.tinkerpop.gremlin.trueno;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.AbstractGraphProvider;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.TestHelper;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.trueno.structure.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoGraphProvider extends AbstractGraphProvider {

    private static final Set<Class> IMPLEMENTATIONS = new HashSet<Class>() {{
        add(TruenoEdge.class);
        add(TruenoElement.class);
        add(TruenoGraph.class);
        add(TruenoProperty.class);
        add(TruenoVertex.class);
        add(TruenoVertexProperty.class);
    }};

    @Override
    public Map<String, Object> getBaseConfiguration(String graphName, Class<?> test, String testMethodName, LoadGraphWith.GraphData graphData) {
        String name = (
                TestHelper.cleanPathSegment(graphName.toLowerCase()) + "_" +
                String.valueOf(Math.abs(TestHelper.cleanPathSegment(test.getSimpleName().toLowerCase()).hashCode())) + "_" +
                String.valueOf(Math.abs(TestHelper.cleanPathSegment(testMethodName.toLowerCase()).hashCode()))
            );

        //System.out.println("----------------- " + name);

        return new HashMap<String, Object>() {{
            put(Graph.GRAPH, TruenoGraph.class.getName());
            put(TruenoGraph.CONFIG_DATABASE, name);
            put(TruenoGraph.CONFIG_SERVER, "http://localhost");
            put(TruenoGraph.CONFIG_PORT, "8000");
        }};

    }

    @Override
    public void clear(Graph graph, Configuration configuration) throws Exception {

    }

    @Override
    public Set<Class> getImplementations() {
        return IMPLEMENTATIONS;
    }
}
