package org.trueno.gremlin.examples;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.json.JSONObject;

import org.trueno.gremlin.structure.*;
import org.trueno.driver.lib.core.data_structures.*;
import org.trueno.driver.lib.core.utils.Pair;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Thread.sleep;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.is;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 * import com.sun.tools.javac.util.Pair;
 */
public class GraphOfTheGods {

    final static String SERVER = "http://localhost";

    public static void traversal1(TruenoGraph graph) {

        GraphTraversalSource g = graph.traversal();

        // Saturn
        Vertex saturn = g.V().has("name", "saturn").next();

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
        final Configuration config = new BaseConfiguration();
        config.setProperty(TruenoGraph.CONFIG_SERVER, SERVER);
        config.setProperty(TruenoGraph.CONFIG_PORT, 8000);
        config.setProperty(TruenoGraph.CONFIG_DATABASE, "titan");

        TruenoGraph graph = TruenoGraph.open(config);
        traversal1(graph);
    }
}
