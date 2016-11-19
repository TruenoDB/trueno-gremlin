package org.trueno.gremlin.structure;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.ivy.util.cli.Option;
import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.trueno.driver.lib.core.TruenoFactory;
import org.trueno.driver.lib.core.data_structures.ComponentType;
import org.trueno.driver.lib.core.data_structures.Filter;
import org.trueno.driver.lib.core.utils.Pair;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// TODO: implement a private forceSync in TruenoGraph, to force that all methods behave as sync
// TODO: implement some methods async (returning promises), eg: Open. This will be useful if the API is used directly in Java (and not iterative via gremlin console)
// TODO: Separate Helper in class, include in one class all funtion/operation that deals with iterators, on the other include more general operations.
/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoHelper {

    private static final Logger logger = LoggerFactory.getLogger(TruenoHelper.class);
    private static final String NOT_FOUND_EXCEPTION = "NotFoundException";

    TruenoHelper() {

    }

    public static void getInstance(final TruenoGraph graph) {
        final Map config = ConfigurationConverter.getMap(graph.configuration);
        /* Get an instance of the graph */
        graph.baseGraph = TruenoFactory.getInstance(config);
        /* Open trueno graph for operations if its stated, otherwise the user has opted to do it programmatically */
        Object async = config.get(TruenoGraph.CONFIG_ASYNC);
        if (async != null) {
            if (async.equals("false")) {
                graph.getBaseGraph().open()
                    .then((result) -> {
                        logger.trace("connected to: {}", config.get(TruenoGraph.CONFIG_DATABASE));
                    })
                    .fail((ex) -> {
                        logger.error("{}", ex);
                    });
            }
        }
    }



    // FIXME: This could be just one function (with argument edge or vertex)
    public static JSONObject getVertex(final TruenoGraph graph, Object id) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

//        System.out.println("getVertex :: " + id);
        Filter filter = graph.getBaseGraph().filter().term("_id", id);
        graph.getBaseGraph().fetch(ComponentType.VERTEX, filter)
            .then(result -> {
                queue.add(result);
//                System.out.println("[getVertex] Filtering: " + filter.toString() + " id: " + id + " -->" + result);
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

//    public static Iterator<Vertex> getAllVertices(final TruenoGraph graph) throws InterruptedException {
//        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);
//
//        graph.getBaseGraph().fetch(ComponentType.VERTEX)
//                .then(result -> {
//                    queue.add(result);
//                })
//                .fail((ex) -> {
//                    throw new Error ("Something bad happened: " + ex);
//                });
//
//        JSONArray list = queue.take();
//
//        return IteratorUtils
//                .stream(list.iterator())
//                .map(o -> (Vertex) new TruenoVertex((JSONObject) o, graph)).iterator();
//    }


    public static Iterator<Vertex> findVertex(final TruenoGraph graph, Optional<String> label) throws InterruptedException {

        Filter filter = graph.getBaseGraph().filter().term("label", label.get());

//        graph.getBaseGraph().fetch(ComponentType.VERTEX, filter)
//                .then(result -> {
//                    System.out.println("[findVertex] result: " + result);
//                }).fail(ex -> {
//            throw new Error ("Something bad happened: " + ex);
//        });

        //return Collections.emptyIterator() ;
        return findVertex(graph, filter);
    }

    public static Iterator<Vertex> findVertex(final TruenoGraph graph, Optional<String> label, String property, Object value) throws InterruptedException {

        String prefix = "prop.";
        Filter filter = graph.getBaseGraph().filter().term(prefix + property, value);

        if (label.isPresent()) {
            filter.term("label", label.get());
        }

        return findVertex(graph, filter);
    }

//    public static Iterator<Vertex> getVertex(final TruenoGraph graph, String property, Object value) throws InterruptedException {
    public static Iterator<Vertex> findVertex(final TruenoGraph graph, final Filter filter) throws InterruptedException {

//        try {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

//        System.out.println("getVertex :: " + id);
//        Filter filter = graph.getBaseGraph().filter().term(property, value);
        System.out.println("\n[findVertex] ");
        graph.getBaseGraph().fetch(ComponentType.VERTEX, filter)
                .then(result -> {
                    System.out.println("[findVertex] result: ");
                    queue.add(result);
                }).fail(ex -> {
                    throw new Error ("Something bad happened: " + ex);
                });


//            System.out.println("echo echo");
            JSONArray list = queue.take();
//            System.out.println("echoooo");
            return IteratorUtils
                    .stream(list.iterator())
                    .map(o -> (Vertex) new TruenoVertex((JSONObject) o, graph)).iterator();
//            return Collections.emptyIterator() ;
//        } catch (Exception e) {
//            System.out.println(e);
//            return Collections.emptyIterator() ;
//        }


    }

    public static Iterator<Vertex> lookupVertices(final TruenoGraph g, final List<HasContainer> hasContainers, final Object... ids) throws InterruptedException {

        System.out.println("lookupVertices [1]!");
        // if ids are present, filter them first
        if (ids.length > 0)
            return IteratorUtils.filter(g.vertices(ids), vertex -> HasContainer.testAll(vertex, hasContainers));
        System.out.println("lookupVertices [2]!");
        // get label
        Optional<String> label = hasContainers.stream()
                .filter(hasContainer -> hasContainer.getKey().equals(T.label.getAccessor()))
                .filter(hasContainer -> Compare.eq == hasContainer.getBiPredicate())
                .map(hasContainer -> (String) hasContainer.getValue())
                .findAny();

        if (label.isPresent()) {
            System.out.println("lookupVertices [3]! --> label is present");
        }

        // if label is present
//        if (label.isPresent()) {
//            System.out.println("label is present!");
            // find by label and key/value
        System.out.println("lookupVertices [4]! " + hasContainers);

        if (label.isPresent()) {

            // find vertex by label and property (key/value)
            for (final HasContainer hasContainer : hasContainers) {

//                System.out.print("lookupVertices --> " + T.label.getAccessor() + " * ");
//
//                System.out.print(">0>hCont:>" + hasContainer + "<0< * ");
//                System.out.print(">2>hCont.getKey:>" + hasContainer.getKey() + "<2< - ");
//                System.out.print(">3>" + hasContainer.getBiPredicate() + "<3< - ");
//                System.out.println(">4>hCont.getVal:>" + hasContainer.getValue() + "<4< * ");

                if (Compare.eq == hasContainer.getBiPredicate() && !hasContainer.getKey().equals(T.label.getAccessor())) {
                    logger.info("find by label and using index");
                    return IteratorUtils.stream(TruenoHelper.findVertex(g, label, hasContainer.getKey(), hasContainer.getValue()))
                            .filter(vertex -> HasContainer.testAll(vertex, hasContainers)).iterator();
                }
            }

            // find vertex by label
            logger.info("find by label");
//            System.out.print(">1>label.get:>" + label.get() + "<1< * ");
            return IteratorUtils.stream(TruenoHelper.findVertex(g, label))
                    .filter(vertex -> HasContainer.testAll(vertex, hasContainers)).iterator();
        } else {

            // find vertex by property (key/value)
            // find vertex by label and property (key/value)
            for (final HasContainer hasContainer : hasContainers) {

//                System.out.print("lookupVertices --> " + T.label.getAccessor() + " * ");
//
//                System.out.print(">0>hCont:>" + hasContainer + "<0< * ");
//                System.out.print(">2>hCont.getKey:>" + hasContainer.getKey() + "<2< - ");
//                System.out.print(">3>" + hasContainer.getBiPredicate() + "<3< - ");
//                System.out.println(">4>hCont.getVal:>" + hasContainer.getValue() + "<4< * ");

                if (Compare.eq == hasContainer.getBiPredicate() && !hasContainer.getKey().equals(T.label.getAccessor())) {
                    logger.info("find using index");
                    return IteratorUtils.stream(TruenoHelper.findVertex(g, label, hasContainer.getKey(), hasContainer.getValue()))
                            .filter(vertex -> HasContainer.testAll(vertex, hasContainers)).iterator();
                }
            }

        }
//            return null; //IteratorUtils.stream()
//        } else {
//            System.out.println("label is not present!");
            // find by doing a linear scan
        logger.info("linear scan");
            return IteratorUtils.filter(g.vertices(), vertex -> HasContainer.testAll(vertex, hasContainers));
//        }
    }

    public static JSONObject getEdge(final TruenoGraph graph, Object id) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(1);

        // FIXME: Edge should have a surrogate identifier, not a compound identifier
        Pair edgeId = (Pair)id;
        Filter filter = graph.getBaseGraph().filter().term("source", edgeId.getFirst()).term("target", edgeId.getSecond());
//        System.out.println("getEdge: filtering --> " + filter);
        graph.getBaseGraph().fetch(ComponentType.EDGE, filter)
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

        graph.getBaseGraph().fetch(ComponentType.VERTEX)
            .then(result -> {
                queue.add(result);
//                System.out.println("getAllVertices --> " + result);
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

//        System.out.println("getAllEdges()");
        graph.getBaseGraph().fetch(ComponentType.EDGE)
                .then(result -> {
                    queue.add(result);
                })
                .fail((ex) -> {
                    throw new Error ("Something bad happened: " + ex);
                });

        JSONArray list = queue.take();
//        System.out.println("getAllEdges() ---> " + list.length());
//        System.out.println("getAllEdges() ---> " + list);

        return IteratorUtils
                .stream(list.iterator())
                .map(o -> (Edge) new TruenoEdge((JSONObject)o, graph)).iterator();
//                .map(o -> (Edge) new TruenoEdge(((JSONObject)o).getJSONObject("_source"), graph)).iterator();
    }

    private static Filter buildFilter(final TruenoVertex vertex, String field, String... edgeLabels) {
        if (edgeLabels.length > 0) {
            String filter = "";
            for (int i=0; i<edgeLabels.length; i++) {
                if (i>0) filter += "|";
                filter += edgeLabels[i];
            }
//            System.out.println("filter --> " + filter);
            return vertex.graph.baseGraph.filter().regexp(field, filter);
        }
//        System.out.println("filter --> null");
        return null;
    }

    public static Iterator<TruenoVertex> getVertices(final TruenoVertex vertex, final Direction direction, final String... edgeLabels) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(2);

//        System.out.println("getVertices() :: " + direction + " :: " + ((edgeLabels.length > 0)?edgeLabels[0]:"null"));
        Filter filter = buildFilter(vertex, "label", edgeLabels);
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().in(ComponentType.VERTEX, null, filter)
                    .then((v) -> {
                        queue.add(v);
//                        System.out.println("getVertices() IN  --> " + v);
                    })
                    .fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        } else {
            /* add an empty array */
            queue.add(new JSONArray());
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().out(ComponentType.VERTEX, null, filter)
                    .then((v) -> {
                        queue.add(v);
//                        System.out.println("getVertices() OUT --> " + v);
                    }).fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        } else {
            /* add an empty array */
            queue.add(new JSONArray());
        }

        // FIXME: If the fetch returns an empty set breaks the execution
        // FIXME: eliminate duplicates
        return IteratorUtils
                .stream(IteratorUtils.concat(queue.take().iterator(), queue.take().iterator()))
                .map(o -> (TruenoVertex) new TruenoVertex((JSONObject)o, vertex.graph)).iterator();
    }

    public static Iterator<TruenoEdge> getEdges(final TruenoVertex vertex, final Direction direction, final String... edgeLabels) throws InterruptedException {
        final BlockingQueue<JSONArray> queue = new ArrayBlockingQueue<JSONArray>(2);

//        System.out.println("getEdges() :: " + direction + " :: " + ((edgeLabels.length > 0)?edgeLabels[0]:"null"));
        Filter filter = buildFilter(vertex, "label", edgeLabels);
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().in(ComponentType.EDGE, filter, null)
                    .then((e) -> {
                        queue.add(e);
//                        System.out.println("getEdges() IN  --> " + e);
                    })
                    .fail((ex) -> {
                        throw new Error ("Something bad happened: " + ex);
                    });
        } else {
            queue.add(new JSONArray());
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().out(ComponentType.EDGE, filter, null)
                    .then((e) -> {
                        queue.add(e);
//                        System.out.println("getEdges() OUT --> " + e);
                    }).fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });
        } else {
            queue.add(new JSONArray());
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
//            System.out.println("persist(): good!");
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

//        System.out.println("edges --> " + edge.getBaseElement());
        vertex.setId(edge.getBaseEdge().getSource());

        return vertex;
    }


    public static org.trueno.driver.lib.core.data_structures.Vertex edgeEnd (final TruenoEdge edge) {
        org.trueno.driver.lib.core.data_structures.Vertex vertex = new org.trueno.driver.lib.core.data_structures.Vertex();

        vertex.setId(edge.getBaseEdge().getTarget());

        return vertex;
    }

//    // String format
//    public static String typeToString()
}
