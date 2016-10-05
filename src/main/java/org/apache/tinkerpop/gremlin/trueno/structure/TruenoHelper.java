package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.jdeferred.DoneFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.trueno.driver.lib.core.data_structures.Component;

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

    TruenoHelper() {

    }

    public static void getInstace(final TruenoGraph graph) {
        graph.getBaseGraph().open().then((result) -> {
           System.out.println("open good!");
        }).fail((ex) -> {
            throw new Error ("Something bad happened: " + ex);
        });
    }

//    public static Iterator<Vertex> getAllVertices(final TruenoGraph graph) {
//        JSONObject result = getAll(graph, null);
//        System.out.println("getAllVertices --> " + result);
//        return IteratorUtils.stream(result.keys())
//                .map(key -> (Vertex) new TruenoVertex((Component) result.get(key), graph)).iterator();
//    }


    // TODO: clean code. http://stackoverflow.com/questions/34184088/how-can-i-return-value-from-function-onresponse-of-retrofit
    public static Iterator<Vertex> getAllVertices(final TruenoGraph graph) throws InterruptedException {
        final BlockingQueue<JSONObject> queue = new ArrayBlockingQueue<JSONObject>(1);

        graph.getBaseGraph().fetch("v")
            .then((result) -> {
                queue.add(result);
//                System.out.println("getAll 1 --> " + result.get("result") + " " + result.get("result").getClass());
            })
            .fail((ex) -> {
                throw new Error ("Something bad happened: " + ex);
            });

        JSONArray list = (JSONArray)queue.take().get("result");
//        System.out.println("getAll 2 --> " + list);

//        Iterator<Object> vtx = list.iterator();
//        for (int i = 0; i<list.length(); i++){
//            System.out.println("[" + i + "] " + list.getJSONObject(i));
//        }

        return IteratorUtils
                .stream(list.iterator())
                .map(o -> (Vertex) new TruenoVertex(((JSONObject)o).getJSONObject("_source"), graph)).iterator();
    }

//    public static JSONObject getAll(final TruenoGraph graph,  Iterator<Vertex> list) {
//        JSONObject empty = new JSONObject();
//
//        graph.getBaseGraph().fetch("v").then(new DoneFilter<JSONObject, JSONObject>() {
//            Iterator<Vertex> list;
//
//            public JSONObject filterDone(JSONObject result) {
//                list = IteratorUtils.stream(result.keys())
//                        .map(key -> (Vertex) new TruenoVertex((Component) result.get(key), graph)).iterator();
//                System.out.println("getAll --> " + result);
//                // fill the iterator
//                // mutex.release();
//            }
//        }).fail((ex) -> {
//            throw new Error ("Something bad happened: " + ex);
//        });
//        return empty;
//    }

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
}
