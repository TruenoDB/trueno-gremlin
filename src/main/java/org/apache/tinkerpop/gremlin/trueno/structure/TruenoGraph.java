package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.NotImplementedException;

import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedGraph;

import org.trueno.driver.lib.core.Trueno;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_PERFORMANCE)

@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.structure.VertexTest$AddEdgeTest",
        method = "*",
        reason = "Trueno gremlin API is under construction.")
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.structure.TransactionTest",
        method = "*",
        reason = "Trueno gremlin API is under construction.")
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.algorithm.generator.CommunityGeneratorTest$ProcessorTest",
        method = "*",
        reason = "Trueno gremlin API is under construction."
)
public class TruenoGraph implements Graph, WrappedGraph<org.trueno.driver.lib.core.data_structures.Graph> {

    /* Trueno Graph API */
    protected Trueno graphAPI;

    /* Graph Component */
    protected org.trueno.driver.lib.core.data_structures.Graph baseGraph;

    /* Config settings for standalone installation */
    public static final String CONFIG_DEFAULT_DB = "trueno";

    public static final String CONFIG_SERVER   = "gremlin.trueno.storage.server";
    public static final String CONFIG_PORT     = "gremlin.trueno.storage.port";
    public static final String CONFIG_DATABASE = "gremlin.trueno.storage.database";
    public static final String CONFIG_CONF     = "gremlin.trueno.storage.conf";

    /* Graph configuration */
    protected BaseConfiguration configuration = new BaseConfiguration();

    /* Database features */
    protected Features features = new TruenoFeatures();

    /**
     * Initialize {@link TruenoGraph} instance.
     *
     * @param graphAPI the instance of TruenoGraph.
     * @param configuration the configuration.
     */
    private void initialize(final Trueno graphAPI, final Configuration configuration) {
        this.graphAPI = graphAPI;
        // TODO: Do some inits on graph object

        /* Establish connection */
        graphAPI.connect(conn -> {
            // TODO: implement an API level function that creates the graph if it doesnot exists.
            /* Open database */
            org.trueno.driver.lib.core.data_structures.Graph graph;
            graph = graphAPI.Graph(configuration.getString(CONFIG_DATABASE));
        }, socket -> {
            /* Disconnect */
            System.out.println("disconnected");
        });
    }

    protected TruenoGraph(final Trueno graphAPI, final Configuration configuration) {
        this.initialize(graphAPI, configuration);
    }

    protected TruenoGraph(final Configuration configuration) {
        this.configuration.copy(configuration);

        /* Create an instance of */
        this.graphAPI = new Trueno(configuration.getString(CONFIG_SERVER), configuration.getInt(CONFIG_PORT));
        // TODO: Create a TruenoFactory to create an instance from the config file (java-driver)
        this.initialize(this.graphAPI, configuration);
    }

    /**
     * Open a new {@link TruenoGraph} instance from a configuration {@link Configuration}.
     *
     * @param configuration the configuration for the instance.
     * @return a newly opened {@link TruenoGraph}
     */
    public static TruenoGraph open(final Configuration configuration) {
        if (null == configuration) throw  Graph.Exceptions.argumentCanNotBeNull("configuration");

        /* Check required configuration parameters */
        if (!configuration.containsKey(CONFIG_SERVER))
            throw new IllegalArgumentException(String.format("Trueno configuration requires that %s to be set", CONFIG_SERVER));

        return new TruenoGraph(configuration);
    }

    /**
     * Open a new {@link TruenoGraph} instance from a configuration file.
     *
     * @param filename the filename and its location.
     * @return a newly opened {@link TruenoGraph}
     * @throws ConfigurationException an Exception in case the file could not be opened.
     */
    public static TruenoGraph open(final String filename) throws ConfigurationException {
        return open(new PropertiesConfiguration(filename));
    }

    /**
     * Construct a {@link TruenoGraph} instance by specifying the hostname and port where the Trueno Core
     * server resides, assuming a local setup.
     *
     * @param hostname the hostname where Trueno Core server resides.
     * @param port the port of connection to the Trueno Core server.
     * @return a newly opened {@link TruenoGraph}
     */
    public static TruenoGraph open(final String hostname, final String port) {
        final Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_SERVER, hostname);
        config.setProperty(CONFIG_PORT, port);
        config.setProperty(CONFIG_DATABASE, CONFIG_DEFAULT_DB);

        return open(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vertex addVertex(final Object... keyValues) {

//        ElementHelper.legalPropertyKeyValueArray(keyValues);
//        if (ElementHelper.getIdValue(keyValues).isPresent())
//            throw Vertex.Exceptions.userSuppliedIdsNotSupported();

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
    public Iterator<Vertex> vertices(Object... vertexIds) {
        // TODO: Move basica functionality to TruenoHelper
        final List<Vertex> vertices = new ArrayList<>();

        /* No predicate (retrieve all nodes) */
        if (0 == vertexIds.length) {
            this.getBaseGraph().fetch("v").whenCompleteAsync((result, err) -> {
                if (result != null) {
                    System.out.println(result);
                } else {
                    System.out.println(result);
                }
            });
        } else {

        }

        return vertices.iterator();
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
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

    @Override
    public org.trueno.driver.lib.core.data_structures.Graph getBaseGraph() {
        return this.baseGraph;
    }

    /**
     * Returns the Trueno Graph API. This class provides access to the principal methods to interact with the database
     * engine.
     *
     * @return the Trueno Graph API {@link Trueno}
     */
    public Trueno getGraphAPI() {
        return this.graphAPI;
    }

}
