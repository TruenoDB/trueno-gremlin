package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.jdeferred.DoneFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trueno.driver.lib.core.Trueno;
import org.trueno.driver.lib.core.TruenoFactory;
import org.trueno.driver.lib.core.data_structures.Component;
import org.trueno.driver.lib.core.data_structures.Filter;
import org.trueno.driver.lib.core.utils.Pair;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.apache.tinkerpop.gremlin.trueno.structure.TruenoGraph.CONFIG_DATABASE;

// TODO: connection using a singleton pattern
// TODO: implement a private forceSync in TruenoGraph, to force that all methods behave as sync

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
// TODO: Separate Helper in class, include in one class all funtion/operation that deals with iterators, on the other include more general operations.
public class TruenoHelper {

    private static final Logger logger = LoggerFactory.getLogger(TruenoHelper.class);
    private static final String NOT_FOUND_EXCEPTION = "NotFoundException";

    TruenoHelper() {

    }

    public static void getInstance(final TruenoGraph graph) {
        final Map config = ConfigurationConverter.getMap(graph.configuration);
        /* Get an instance of the graph */
        graph.baseGraph = TruenoFactory.getInstance(config);
        /* Open trueno graph for operations */
        graph.getBaseGraph().open()
            .then((result) -> {
                System.out.println("open good!");
            }).fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });
    }

//    public static void getInstace(final TruenoGraph graph) {
//        final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
//
//        /* Establish connection */
//        Trueno trueno = graph.getGraphAPI();
//        trueno.connect(
//                /* on Connect */
//                (socket) -> {
//                    String id = socket.id();
//                    graph.setSocketId(id);
//                    System.out.println("[" + id + "] connected:  " + graph.configuration.getString(CONFIG_DATABASE));
//
//                    /* Create or Open an database instance */
//                    graph.baseGraph = trueno.Graph(graph.configuration.getString(CONFIG_DATABASE));
//                    graph.getBaseGraph().open()
//                        .then((result) -> {
//                            System.out.println("open good!");
//                            queue.add(new Integer(1));
//                        }).fail((ex) -> {
//                            throw new Error ("Something bad happened: " + ex);
//                        });
//
//                },
//                /* on Disconnect */
//                (socket) -> {
//                    System.out.println("[" + socket.id() + "] disconnected");
//                });
//
//        /* Block the execution until the instance is got */
//        try {
//            queue.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    // FIXME: This could be just one function (with argument edge or vertex)
    public static JSONObject getVertex(final TruenoGraph graph, Object id) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

        Filter filter = graph.getBaseGraph().filter().term("_id", id);
        graph.getBaseGraph().fetch("v", filter)
            .then(result -> {
                queue.add(result);
                System.out.println("[getVertex] Filtering: " + filter.toString() + " id: " + id + " -->" + result);
            }).fail(ex -> {
            throw new Error ("Something bad happened: " + ex);
        });

        try {
            //.put("id", ((JSONObject)o).get("_id"))
            JSONObject json = queue.take().getJSONObject(0);
            return json;
        } catch (JSONException e) {
           // e.printStackTrace();
            throw new RuntimeException(NOT_FOUND_EXCEPTION);
        }
    }

    public static JSONObject getEdge(final TruenoGraph graph, Object id) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

        // FIXME: Edge should have a surrogate identifier, not a compound identifier
        Pair edgeId = (Pair)id;
        Filter filter = graph.getBaseGraph().filter().term("source", edgeId.getFirst()).term("target", edgeId.getSecond());
        System.out.println("getEdge: filtering --> " + filter);
        graph.getBaseGraph().fetch("e", filter)
                .then(result -> {
                    queue.add(result);
                    //System.out.println("Filtering: " + filter + " id: " + id + " -->" + result);
                }).fail(ex -> {
            throw new Error ("Something bad happened: " + ex);
        });

        try {
            // FIXME: There sould be a method to extract the JSON from the JSONArray
            JSONObject json = queue.take().getJSONObject(0);
            return json;
        } catch (JSONException e) {
            // e.printStackTrace();
            throw new RuntimeException(NOT_FOUND_EXCEPTION);
        }
    }

//    private xxx (Object obj) {
//        JSONObject in = (JSONObject)obj;
//        JSONObject out = in.getJSONObject("_source");
//        out.p
//
//    }

    // FIXME: Standarize the types returned by the helper: either gremlin types or trueno types, but not a mix.
    // TODO: clean code. http://stackoverflow.com/questions/34184088/how-can-i-return-value-from-function-onresponse-of-retrofit
    public static Iterator<Vertex> getAllVertices(final TruenoGraph graph) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

        graph.getBaseGraph().fetch("v")
            .then(result -> {
                queue.add(result);
                System.out.println("getAllVertices --> " + result);
            })
            .fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });

        JSONArray list = queue.take();

        return IteratorUtils
                .stream(list.iterator())
                .map(o -> (Vertex) new TruenoVertex((JSONObject) o, graph)).iterator();
//                .map(o -> (Vertex) new TruenoVertex(((JSONObject)o).getJSONObject("_source").put("id", ((JSONObject)o).get("_id")), graph)).iterator();
    }

    // TODO: clean code.
    public static Iterator<Edge> getAllEdges(final TruenoGraph graph) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

        System.out.println("getAllEdges()");
        graph.getBaseGraph().fetch("e")
                .then(result -> {
                    queue.add(result);
                })
                .fail((ex) -> {
                    throw new Error ("Something bad happened: " + ex);
                });

        JSONArray list = queue.take();
        System.out.println("getAllEdges() ---> " + list.length());
        System.out.println("getAllEdges() ---> " + list);

        return IteratorUtils
                .stream(list.iterator())
                .map(o -> (Edge) new TruenoEdge((JSONObject)o, graph)).iterator();
//                .map(o -> (Edge) new TruenoEdge(((JSONObject)o).getJSONObject("_source"), graph)).iterator();
    }

    private static Filter buildFilter(final TruenoVertex vertex, String field, String... edgeLabels) {
        if (edgeLabels.length > 0) {
            String filter = "";
            for (int i=0; i<edgeLabels.length; i++) {
                if (i>1) filter += "|";
                filter += edgeLabels[i];
            }
            System.out.println("filter --> " + filter);
            return vertex.graph.baseGraph.filter().regexp(field, filter);
        }
        System.out.println("filter --> null");
        return null;
    }

    public static Iterator<TruenoVertex> getVertices(final TruenoVertex vertex, final Direction direction, final String... edgeLabels) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(2);

        System.out.println("getVertices() :: " + direction + " :: " + ((edgeLabels.length > 0)?edgeLabels[0]:"null"));
        Filter filter = buildFilter(vertex, "label", edgeLabels);
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().in("v", filter)
                    .then((v) -> {
                        queue.add(v);
                        System.out.println("getVertices() IN  --> " + v);
                    })
                    .fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().out("v", filter)
                    .then((v) -> {
                        queue.add(v);
                        System.out.println("getVertices() OUT --> " + v);
                    }).fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        }

        // FIXME: If the fetch returns an empty set breaks the execution
        // FIXME: eliminate duplicates
        return IteratorUtils
                .stream(IteratorUtils.concat(queue.take().iterator(), queue.take().iterator()))
                .map(o -> (TruenoVertex) new TruenoVertex((JSONObject)o, vertex.graph)).iterator();
    }

    public static Iterator<TruenoEdge> getEdges(final TruenoVertex vertex, final Direction direction, final String... edgeLabels) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(2);

        System.out.println("getEdges() :: " + direction + " :: " + ((edgeLabels.length > 0)?edgeLabels[0]:"null"));
        Filter filter = buildFilter(vertex, "label", edgeLabels);
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().in("e", filter)
                    .then((e) -> {
                        queue.add(e);
                        System.out.println("getEdges() IN  --> " + e);
                    })
                    .fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().out("e", filter)
                    .then((e) -> {
                        queue.add(e);
                        System.out.println("getEdges() OUT --> " + e);
                    }).fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });
        }


        // FIXME: eliminate duplicates
        return IteratorUtils
                .stream(IteratorUtils.concat(queue.take().iterator(), queue.take().iterator()))
                .map(o -> (TruenoEdge) new TruenoEdge((JSONObject)o, vertex.graph)).iterator();
    }

    public static void destroy(final TruenoElement trueno) {

        trueno.graph.getBaseGraph().destroy().then((result) -> {

        }).fail((ex) -> {
           throw new Error ("Something bad happened: " + ex);
        });
    }

    public static void persist(final TruenoElement element) {

        element.getBaseElement().persist().then((result) -> {
            System.out.println("persist(): good!");
        }).fail((ex) -> {
            throw new Error ("Something bad happened: " + ex);
        });
    }

    // TODO: Create exceptions as clases, and not just message (evaluate)
    public static boolean isNotFound(final RuntimeException ex) {
//        return ex.getClass().getSimpleName().equals(NOT_FOUND_EXCEPTION);
        return ex.getMessage().equals(NOT_FOUND_EXCEPTION);
    }


    // --- stream helpers
    // As in orientdb driver
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

//    public static Stream<String> asStream(String[] fieldNames) {
//        return new ArrayList(fieldNames).stream();
//    }


    // FIXME. Implement Java setters as chaining methods (eg. http://stackoverflow.com/questions/15054237/oop-in-java-class-inheritance-with-method-chaining?rq=1)
    // FIXME. Create an interface that represent TruenoRelationship and TruenoNode, and implement this methods.
    // -- edges helpers
    public static org.trueno.driver.lib.core.data_structures.Vertex edgeStart (final TruenoEdge edge) {
        org.trueno.driver.lib.core.data_structures.Vertex vertex = new org.trueno.driver.lib.core.data_structures.Vertex();

        vertex.setId(edge.getBaseEdge().getSource());

        return vertex;
    }


    public static org.trueno.driver.lib.core.data_structures.Vertex edgeEnd (final TruenoEdge edge) {
        org.trueno.driver.lib.core.data_structures.Vertex vertex = new org.trueno.driver.lib.core.data_structures.Vertex();

        vertex.setId(edge.getBaseEdge().getTarget());

        return vertex;
    }
}
