package org.trueno.gremlin.examples;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.trueno.driver.lib.core.Trueno;
import org.trueno.driver.lib.core.TruenoFactory;
import org.trueno.gremlin.structure.TruenoGraph;
import org.trueno.gremlin.structure.TruenoGraphFactory;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 * Example Graph based on Konigsberg Problem (seven bridges).
 * @see {<a href="https://en.wikipedia.org/wiki/Seven_Bridges_of_K%C3%B6nigsberg">
 * Seven Bridges of Königsberg</a>}
 *
 */
public class Konigsberg {

    final static String SERVER = "http://localhost";
    final static String DATABASE = "sevenbridges";

    public static void traversal1(TruenoGraph graph) {
        System.out.println("Traversal");
        GraphTraversalSource g = graph.traversal();

        g.E().valueMap().forEachRemaining(e -> {
            System.out.println(e);
        });

        System.out.println(".....");
    }



    public static void main(String[] args) throws Exception {

        System.out.println("Seven Bridges Graph (example)");

        TruenoGraph graph = TruenoGraph.open(DATABASE);
        //TruenoGraphFactory.generateKonigsberg(graph);

        traversal1(graph);
    }
}
