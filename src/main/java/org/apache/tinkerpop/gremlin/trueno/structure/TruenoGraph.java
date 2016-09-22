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


    /* Database features */
    protected Features features = new TruenoFeatures();

    protected Trueno baseGraph;

    /* Config settings for standalone installation */
    public static final String CONFIG_SERVER = "gremlin.trueno.server";
    public static final String CONFIG_PORT = "gremlin.trueno.port";
    public static final String CONFIG_CONF = "gremlin.trueno.conf";


    protected BaseConfiguration configuration = new BaseConfiguration();

    /**
     * Initialize {@link TruenoGraph} instance.
     *
     * @param graph the instance of TruenoGraph.
     * @param configuration the configuration.
     */
    private void initialize(final Trueno graph, final Configuration configuration) {
        this.baseGraph = graph;
        // TODO: Do some inits on graph object
    }

    protected TruenoGraph(final Trueno baseGraph, final Configuration configuration) {
        this.initialize(baseGraph, configuration);
    }

    protected TruenoGraph(final Configuration configuration) {
        this.configuration.copy(configuration);

        /* Create an instance of */
        this.baseGraph = new Trueno(configuration.getString(CONFIG_SERVER), configuration.getInt(CONFIG_PORT));
        // TODO: Create a TruenoFactory to create an instance from the config file (java-driver)
        this.initialize(this.baseGraph, configuration);
    }

    /**
     * Open a new {@link TruenoGraph} instance.
     *
     * @param configuration the configuration for the instance.
     * @return a newly opened {@link org.apache.tinkerpop.gremlin.structure.Graph}
     */
    public static TruenoGraph open(final Configuration configuration) {
        if (null == configuration) throw  Graph.Exceptions.argumentCanNotBeNull("configuration");

        /* Check required configuration parameters */
        if (!configuration.containsKey(CONFIG_SERVER))
            throw new IllegalArgumentException(String.format("Trueno configuration requires that %s to be set", CONFIG_SERVER));

        return new TruenoGraph(configuration);
    }

    /**
     * Construct a TruenoGraph instance by specifying the hostname and port where the Trueno Core server resides,
     * assuming a local setup.
     *
     * @param hostname the hostname where Trueno Core server resides.
     * @param port the port of connection to the Trueno Core server.
     * @return a newly opened {@link org.apache.tinkerpop.gremlin.structure.Graph}
     */
    public static TruenoGraph open(final String hostname, final String port) {
        final Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_SERVER, hostname);
        config.setProperty(CONFIG_PORT, port);
        return open(config);
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

//        this.baseGraph.createVertex(ElementHelper.getLabelValue(keyValues));

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
        // TODO: implement shutdown graph.
    }

    @Override
    public Variables variables() {
        throw new NotImplementedException();
    }

    @Override
    public Configuration configuration() {
        return this.configuration;
    }

    @Override
    public Features features() {
        return features;
    }

    /**
     * Returns an instance of the API of Trueno Graph.
     *
     * @return the Trueno {@link org.trueno.driver.lib.core.Trueno} instance.
     */
    public Trueno getBaseGraph() {
        return this.baseGraph;
    }

}
