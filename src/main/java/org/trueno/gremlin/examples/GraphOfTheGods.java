package org.trueno.gremlin.examples;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.trueno.gremlin.structure.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.is;

/**
 * Example Graph based on the roman mythology (taken from Titan:db).
 * The traversal example are based on Titan:db documentation. For more references see:
 * @see <a href="http://s3.thinkaurelius.com/docs/titan/1.0.0/getting-started.html">Titan:db. Getting Started</a>
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class GraphOfTheGods {

    final static String SERVER = "http://localhost";
    final static String DATABASE = "titan";

    public static void traversal1(TruenoGraph graph) {

        GraphTraversalSource g = graph.traversal();

        // Saturn
        Vertex saturn = g.V().has("name", "saturn").next();
        System.out.println(saturn);

        // Get all vertices
        System.out.println(".....");
        g.V().valueMap().forEachRemaining( vertex -> {
            System.out.println("vertex: " + vertex);
        });

        // Saturn
        // Expected => {name=[saturn], age=[10000]}
        System.out.println(".....");
        g.V(saturn).valueMap().forEachRemaining(value -> {
            System.out.println(value);
        });

        // Saturn son
        // Expected => jupiter
        System.out.println(".....");
        g.V(saturn).in().values("name").forEachRemaining(name -> {
            System.out.println("name: " + name);
        });

        // Saturn grandson
        // Expected => Hercules
        System.out.println(".....");
        g.V(saturn).in("father").in("father").values("name").forEachRemaining( name -> {
            System.out.println("name: " + name);
        });

        System.out.println(".....");
        g.E().has("reason", "loves waves").inV().in().select("name").forEachRemaining(v -> {
            System.out.println(v);
        });

        System.out.println(".....");
        Vertex hercules = g.V(saturn).repeat(__.in("father")).times(2).next();
        System.out.println(hercules);
        System.out.println(".....");
        g.V(hercules).out("father", "mother").values("name").forEachRemaining(name -> {
            System.out.println(name);
        });

        System.out.println(".....");
        g.V(hercules).out("father", "mother").label().forEachRemaining(label -> {
            System.out.println(label);
        });

        System.out.println(".....");
        g.V(hercules).out("battled").valueMap().forEachRemaining(value -> {
            System.out.println(value);
        });

        System.out.println(".....");
        g.V(hercules).outE("battled").has("time", P.gt(1)).inV().values("name").forEachRemaining(name -> {
            System.out.println(name);
        });

        /* Cohabiters of Tartarus */
        System.out.println(".....");
        Vertex pluto = g.V().has("name", "pluto").next();
        /* who are pluto's cohabitans */
        g.V(pluto).out("lives").in("lives").values("name").forEachRemaining(name -> {
            System.out.println(name);
        });
        /* pluto can't be his own cohabitant */
        System.out.println(".....");
        g.V(pluto).out("lives").in("lives").where(is(P.neq(pluto))).values("name").forEachRemaining(name -> {
            System.out.println(name);
        });
        System.out.println(".....");
        g.V(pluto).as("x").out("lives").in("lives").where(P.neq("x")).values("name").forEachRemaining(name -> {
            System.out.println(name);
        });

        /* where do pluto's brothers live? */
        System.out.println(".....");
        g.V(pluto).out("brother").out("lives").values("name").forEachRemaining(name -> {
            System.out.println(name);
        });
        /* which brother lives in which place? */
        System.out.println(".....");
        g.V(pluto).out("brother").as("god").out("lives").as("place").select("god", "place").forEachRemaining(place -> {
            System.out.println(place);
        });
        /* what is the name of the brother and the name of the place? */
        System.out.println(".....");
        g.V(pluto).out("brother").as("god").out("lives").as("place").select("god", "place").by("name").forEachRemaining(place -> {
            System.out.println(place);
        });

        System.out.println(".....");
        g.V(pluto).outE("lives").values("reason").forEachRemaining(name -> {
            System.out.println(name);
        });

        System.out.println(" < <  E O F  > > ");
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Graph of the God (Titan example)");

        TruenoGraph graph = TruenoGraph.open(DATABASE);
        traversal1(graph);
    }
}
