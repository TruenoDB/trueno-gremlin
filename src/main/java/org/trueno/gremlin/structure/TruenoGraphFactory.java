package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TruenoGraphFactory} creates populated instance of {@link TruenoGraph}. Basically, it's aimed to be used for
 * test purpose.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoGraphFactory {

    private static final Logger logger = LoggerFactory.getLogger(TruenoHelper.class);

    /**
     * Populate an {@link TruenoGraph} with the elements (vertices and edges) of the Koninsberg example (seven bridges
     * problem)
     *
     * @param graph the TruenoGraph instance.
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

    /**
     * Open an {@link TruenoGraph} instance and populate it with the the elemeents of the Koninsberg example (seven
     * bridges problem).
     *
     * @param graph the TruenoGraph instance.
     */
    public static void openAndGenerateKonigsberg(TruenoGraph graph) {
        String database = "Seven Bridges database";
        graph.open()
                .then( result -> {
                    logger.info("Loading data into {}", database);
                    generateKonigsberg(graph);
                })
                .fail( error -> {
                    logger.info("Error while trying to load data into {}", database);
                    logger.trace("{}", error);
                });
    }

}
