package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.NotImplementedException;

import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.trueno.driver.lib.core.Trueno;
import org.trueno.driver.lib.core.communication.Callback;
//import org.trueno.driver.lib.core.data_structures.Graph;

import java.util.Iterator;


/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_PERFORMANCE)
public class TruenoGraph implements Graph {


    protected Trueno trueno;

    public static final String CONFIG_SERVER = "gremlin.trueno.server";
    public static final String CONFIG_CONF = "gremlin.trueno.conf";


    protected BaseConfiguration configuration = new BaseConfiguration();

    private void initialize(final Configuration configuration) {

        this.configuration.copy(configuration);

        /* instantiate Trueno driver */
        trueno  = new Trueno("http://localhost", 8000);
    }

    /**
     *
     * @param configuration
     */
    protected TruenoGraph(final TruenoGraph baseGraph, final Configuration configuration) {
        this.initialize(configuration);
    }

    protected TruenoGraph(final Configuration configuration) {
        this.initialize(configuration);
    }

    /**
     * Open a new {@link TruenoGraph} instance.
     *
     * @param configuration the configuration for the instance.
     * @return a newly opened {@link org.apache.tinkerpop.gremlin.structure.Graph}
     */
    public static TruenoGraph open (final Configuration configuration) {
        if (null == configuration) throw  Graph.Exceptions.argumentCanNotBeNull("configuration");

        /* Check configuration parameters */
//        if (!configuration.containsKey())

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vertex addVertex(final Object... keyValues) {

        ElementHelper.legalPropertyKeyValueArray(keyValues);
        if (ElementHelper.getIdValue(keyValues).isPresent())
            throw Vertex.Exceptions.userSuppliedIdsNotSupported();

//        // Neo4JGraphAPIImpl
//        // https://github.com/neo4j-contrib/neo4j-tinkerpop-api-impl/blob/3.0/src/main/java/org/neo4j/tinkerpop/api/impl/Neo4jGraphAPIImpl.java
//        final Neo4jVertex vertex =
//                new Neo4jVertex(this.baseGraph.createNode(ElementHelper.getLabelValue(keyValues).orElse(Vertex.DEFAULT_LABEL).split(Neo4jVertex.LABEL_DELIMINATOR)), this);

        final TruenoVertex vertex = null;
        // TODO: implements createNode() in java-driver
        ElementHelper.attachProperties(vertex, keyValues);
        return vertex;
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> aClass) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Iterator<Vertex> vertices(Object... objects) {
        return null;
    }

    @Override
    public Iterator<Edge> edges(Object... objects) {
        return null;
    }

    @Override
    public Transaction tx() {

        throw new NotImplementedException();
    }

    /**
     * Closing a {@code Graph} is equivalet to "shutdown" and implies that no further operations can be executed on
     * the instance.
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

        // TODO: implement close graph.
    }

    @Override
    public Variables variables() {

        throw new NotImplementedException();
    }

    @Override
    public Configuration configuration() {
        return null;
    }

}
