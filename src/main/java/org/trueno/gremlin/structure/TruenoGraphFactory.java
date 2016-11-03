package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Created by ebarsallo on 11/2/16.
 */
public class TruenoGraphFactory {

    /**
     *
     * @param graph
     *
     * @see {<a href="https://upload.wikimedia.org/wikipedia/commons/4/40/K%C3%B6nigsberg_Stadtplan_1905.svg">
     *     Map of Königsberg, 1905</a>}
     */
    public static void generateKonigsberg(TruenoGraph graph) {

        /* vertices */
        Vertex v1 = graph.addVertex(T.id, 1, T.label, "land", "name", "A");
        Vertex v2 = graph.addVertex(T.id, 2, T.label, "land", "name", "B");
        Vertex v3 = graph.addVertex(T.id, 3, T.label, "land", "name", "C");
        Vertex v4 = graph.addVertex(T.id, 4, T.label, "land", "name", "D");

        /* edges */
        v1.addEdge("bridge", v3, T.id, 1, "name", "Traders Bridge", "german", "Kramerbrucke");
        v1.addEdge("bridge", v2, T.id, 2, "name", "Green Bridge", "german", "Grüne Brücke");
        v1.addEdge("bridge", v4, T.id, 3, "name", "Cathedral Bridge", "german", "Dombrücke");

        v2.addEdge("bridge", v1, T.id, 4, "name", "Dung Bridge", "german", "Köttelbrücke");
        v2.addEdge("bridge", v4, T.id, 5, "name", "Wood Bridge", "german", "Holzbrücke");

        v3.addEdge("bridge", v1, T.id, 6, "name", "Forged Bridge", "german", "Schmiedebrücke");
        v3.addEdge("bridge", v4, T.id, 7, "name", "High Bridge", "german", "Hohe Brücke");
    }

}
