package org.trueno.gremlin.examples;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.trueno.gremlin.structure.TruenoGraph;

/**
 * Example Graph based on a Arvix HEP-TH (high energy physics theory) Citation Network.
 * @see {<a href="https://snap.stanford.edu/data/cit-HepTh.html">HEP-TH Network from SNAP</a>}
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class Citations {

    final static String SERVER = "http://localhost";
    final static String DATABASE = "citations";

    public static void traversal1(TruenoGraph graph) {

        System.out.println("Traversal");

        GraphTraversalSource g = graph.traversal();
        System.out.println("count: " + g.V().count().next());

        Vertex v1 = g.V(9408046).next();
        System.out.println("v1 -> " + v1);

        System.out.println(".....");
        g.V(v1).out().valueMap().forEachRemaining(value -> {
            System.out.println(value);
        });
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Citations Graph (example)");

        TruenoGraph graph = TruenoGraph.open(DATABASE);
        traversal1(graph);
    }
}
