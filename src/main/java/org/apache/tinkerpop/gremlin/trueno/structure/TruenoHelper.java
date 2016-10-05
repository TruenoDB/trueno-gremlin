package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.jdeferred.DoneFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.trueno.driver.lib.core.data_structures.Component;
import org.trueno.driver.lib.core.data_structures.Filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoHelper {

    private static final String NOT_FOUND_EXCEPTION = "NotFoundException";

    TruenoHelper() {

    }

    public static void getInstace(final TruenoGraph graph) {
        graph.getBaseGraph().open().then((result) -> {
           System.out.println("open good!");
        }).fail((ex) -> {
            throw new Error ("Something bad happened: " + ex);
        });
    }

    public static JSONObject getVertex(final TruenoGraph graph, Object id) throws InterruptedException {
        final BlockingQueue<JSONObject> queue = new ArrayBlockingQueue<JSONObject>(1);

        Filter filter = graph.getBaseGraph().filter().term("id", id);
        graph.getBaseGraph().fetch("v", filter)
            .then(result -> {
                queue.add(result);
                //System.out.println("Filtering: " + filter + " id: " + id + " -->" + result);
            }).fail(ex -> {
            throw new Error ("Something bad happened: " + ex);
        });

        try {
            JSONObject json = ((JSONArray)queue.take().get("result")).getJSONObject(0).getJSONObject("_source");
            return json;
        } catch (JSONException e) {
           // e.printStackTrace();
            throw new RuntimeException(NOT_FOUND_EXCEPTION);
        }
    }

    // TODO: clean code. http://stackoverflow.com/questions/34184088/how-can-i-return-value-from-function-onresponse-of-retrofit
    public static Iterator<Vertex> getAllVertices(final TruenoGraph graph) throws InterruptedException {
        final BlockingQueue<JSONObject> queue = new ArrayBlockingQueue<JSONObject>(1);

        graph.getBaseGraph().fetch("v")
            .then(result -> {
                queue.add(result);
            })
            .fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });

        JSONArray list = (JSONArray)queue.take().get("result");

        return IteratorUtils
                .stream(list.iterator())
                .map(o -> (Vertex) new TruenoVertex(((JSONObject)o).getJSONObject("_source"), graph)).iterator();
    }

    public static Iterator<TruenoVertex> getVertices(final TruenoVertex vertex, final Direction direction, final String...edgeLabels) {
        final List<Vertex> vertices = new ArrayList<>();

        // TODO: filter results using edgeLabels
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().neighbors("v", null, "in").then((result) -> {
                System.out.println(result);
            }).fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().neighbors("v", null, "out").then((result) -> {
                System.out.println(result);
            }).fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });
        }

        return (Iterator)vertices.iterator();
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
}
