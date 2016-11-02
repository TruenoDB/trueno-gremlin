package org.trueno.gremlin.structure;

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
import org.trueno.driver.lib.core.utils.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_PERFORMANCE)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_ENVIRONMENT)
public class TruenoGraph implements Graph, WrappedGraph<org.trueno.driver.lib.core.data_structures.Graph> {

    /* Trueno Graph API */
    protected Trueno graphAPI;

    private String socketId;
    private TruenoGraphVariables truenoGraphVariables;

    /* Graph Component */
    protected org.trueno.driver.lib.core.data_structures.Graph baseGraph;

    /* Config settings for standalone installation */
    public static final String CONFIG_DEFAULT_DB = "trueno";

    public static final String CONFIG_SERVER   = "trueno.storage.server";
    public static final String CONFIG_PORT     = "trueno.storage.port";
    public static final String CONFIG_DATABASE = "trueno.storage.database";
    public static final String CONFIG_CONF     = "trueno.storage.conf";

    /* Graph configuration */
    protected BaseConfiguration configuration = new BaseConfiguration();

    /* Database features */
    protected Features features = new TruenoFeatures();

    /**
     * Sets the socket identifier for the connection.
     *
     * @param socket the socket string (identifier)
     */
    protected void setSocketId(String socket) {
        this.socketId = socket;
    }

    protected String getSocketId() {
        return this.socketId;
    }

    /**
     * Initialize {@link TruenoGraph} instance.
     *
     * @param graphAPI the instance of TruenoGraph.
     * @param configuration the configuration.
     */
    private void initialize(final Trueno graphAPI, final Configuration configuration) {
        /* Init GraphAPI and Trueno 'raw' Graph object */
        this.graphAPI  = graphAPI;
        /* Open database and get an instance (or create it) from the database */
        TruenoHelper.getInstance(this);
        /* Initialize graph */
        this.truenoGraphVariables = new TruenoGraphVariables(this);
    }

    protected TruenoGraph(final Trueno graphAPI, final Configuration configuration) {
        this.initialize(graphAPI, configuration);
    }

    protected TruenoGraph(final Configuration configuration) {
        this.configuration.copy(configuration);
        /* Create an instance of */
        Trueno trueno = new Trueno(configuration.getString(CONFIG_SERVER), configuration.getInt(CONFIG_PORT));
        /* Establish connection and get and instance of the database */
        this.initialize(trueno, configuration);
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
            throw new IllegalArgumentException(String.format("Trueno configuration requires %s to be set", CONFIG_SERVER));

        return new TruenoGraph(configuration);
    }

    /**
     * Open a new {@link TruenoGraph} instance from a configuration file.
     *
     * @param database the trueno database name.
     * @return a newly opened {@link TruenoGraph}
     * @throws ConfigurationException an Exception in case the file could not be opened.
     */
    public static TruenoGraph open(final String database) throws ConfigurationException {
        final Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_SERVER, Trueno.DEFAULT_HOST);
        config.setProperty(CONFIG_PORT, Trueno.DEFAULT_PORT);
        config.setProperty(CONFIG_DATABASE, database);

        return open(config);
    }

    /**
     * Open a new {@link TruenoGraph} instance from a configuration file.
     *
     * @param file the file which contains the respective configuration.
     * @return a newly opened {@link TruenoGraph}
     * @throws ConfigurationException an Exception in case the file could not be opened.
     */
    public static TruenoGraph open(final File file) throws ConfigurationException {
        return open(new PropertiesConfiguration(file));
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

        final TruenoVertex vertex = new TruenoVertex(this.baseGraph.addVertex(), this);

        if (!ElementHelper.getIdValue(keyValues).isPresent()) {
            throw new RuntimeException("Id Not Supplied");
        }
        ElementHelper.attachProperties(vertex, keyValues);
        vertex.getBaseVertex().setId(ElementHelper.getIdValue(keyValues).get());
//        System.out.println("addVertex --> " + vertex.getBaseVertex());
        TruenoHelper.persist(vertex);
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
        // TODO: Move basic functionality to TruenoHelper
        final List<Vertex> vertices = new ArrayList<>();

        /* No predicate (retrieve all nodes) */
        if (0 == vertexIds.length) {
            try {
                return TruenoHelper.getAllVertices(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ElementHelper.validateMixedElementIds(Vertex.class, vertexIds);
            return Stream.of(vertexIds)
                    .map(id -> {
                        if (id instanceof Number) {
                            return ((Number) id).longValue();
                        } else if (id instanceof String) {
                            return Long.valueOf(id.toString());
                        } else if (id instanceof Vertex) {
                            return ((Vertex) id).id();
                        } else {
                            throw new IllegalArgumentException("Unknown vertex id type: " + id);
                        }
                    })
                    .flatMap(id -> {
                        try {
                            return Stream.of(TruenoHelper.getVertex(this, id));
                        } catch (final RuntimeException e) {
                            if (TruenoHelper.isNotFound(e)) return Stream.empty();
                            e.printStackTrace();
                            throw e;
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                            return Stream.empty();
                        }
                    })
                    .map(node -> (Vertex) new TruenoVertex(node, this)).iterator();
        }
        return vertices.iterator();
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        // TODO: Move basic functionality to TruenoHelper
        final List<Edge> edges = new ArrayList<>();

        /* No predicate (retrieve all nodes) */
        if (0 == edgeIds.length) {
            try {
                return TruenoHelper.getAllEdges(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ElementHelper.validateMixedElementIds(Vertex.class, edgeIds);
            return Stream.of(edgeIds)
                    .map(id -> {
                        // TODO: Temporaly, the edge only support an id of type Pair
                        if (id instanceof Pair) {
                            return id;
                        } else if (id instanceof Edge) {
                            return ((Edge) id).id();
                        }else {
                            throw new IllegalArgumentException("Unknown vertex id type: " + id);
                        }
                    })
                    .flatMap(id -> {
                        try {
                            return Stream.of(TruenoHelper.getEdge(this, id));
                        } catch (final RuntimeException e) {
                            if (TruenoHelper.isNotFound(e)) return Stream.empty();
                            e.printStackTrace();
                            throw e;
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                            return Stream.empty();
                        }
                    })
                    .map(node -> (Edge) new TruenoEdge(node, this)).iterator();
        }
        return edges.iterator();
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
        /* disconnect socket */
        if (this.getBaseGraph() != null)
        this.getBaseGraph().close();
    }

    @Override
    public Variables variables() {
        return this.truenoGraphVariables;
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
