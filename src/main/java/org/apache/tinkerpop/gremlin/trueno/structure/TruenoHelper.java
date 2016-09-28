package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoHelper {

    TruenoHelper() {

    }

    public static Iterator<TruenoVertex> getVertices(final TruenoVertex vertex, final Direction direction, final String...edgeLabels) {
        final List<Vertex> vertices = new ArrayList<>();

        // TODO: filter results using edgeLabels
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().neighbors("v", null, "in").whenComplete((result, err) -> {
               if (result != null) {
                   System.out.println(result);
               } else {
                   System.out.println(result);
               }
            });
        }

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            vertex.getBaseVertex().neighbors("v", null, "out").whenComplete((result, err) -> {
                if (result != null) {
                    System.out.println(result);
                } else {
                    throw new Error("Something bad happened: ", err);
                }
            });
        }

        return (Iterator)vertices.iterator();
    }
}
