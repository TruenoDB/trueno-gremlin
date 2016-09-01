package org.apache.tinkerpop.gremlin.trueno.structure;

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
public class TruenoGraph implements Graph {


    protected Trueno trueno;


    /**
     *
     * @param configuration
     */
    public TruenoGraph (final Configuration configuration) {

        // TODO:

        // TODO: constructor
        initialize();

    }

    private void initialize() {

        /* instantiate Trueno driver */
        trueno  = new Trueno("http://localhost", 8000);


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
