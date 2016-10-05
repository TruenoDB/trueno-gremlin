import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.trueno.structure.TruenoGraph;
import org.apache.tinkerpop.gremlin.trueno.structure.TruenoHelper;
import org.apache.tinkerpop.gremlin.trueno.structure.TruenoVertex;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Thread.sleep;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class GraphTest {

    public static void main(String[] args) throws Exception {
        System.out.println("testing ...");
        final Configuration config = new BaseConfiguration();
        config.setProperty(TruenoGraph.CONFIG_SERVER, "http://localhost");
        config.setProperty(TruenoGraph.CONFIG_PORT, 8000);
        config.setProperty(TruenoGraph.CONFIG_DATABASE, "graphi");

        //TruenoGraph g = TruenoGraph.open("http://localhost", "8000");
        TruenoGraph g = TruenoGraph.open(config);
        sleep(1000);
        if (g.getGraphAPI().isConnected()) {

            System.out.println("connected! " + g.getBaseGraph().getRef());

            HashMap<String, String> props = new HashMap<>();
            props.put("label", "principal");

            Vertex vertex1 = g.addVertex(T.id, 10);
            Vertex vertex2 = g.addVertex(T.id, 11);
            Vertex vertex3 = g.addVertex(T.id, 12);

            /* Retrieve vertices without filter */
            System.out.println("Retrieve all vertices");
            Iterator<Vertex> v1 = g.vertices();
            while (v1.hasNext()) {
                System.out.println("vertice [1]: " + v1.next());
            }

            /* Retrieve using some filter */
            System.out.println("Retrieve some vertices using a filter");
            Iterator<Vertex> v2 = g.vertices(5,4);
            while (v2.hasNext()) {
                System.out.println("vertice [2]: " + v2.next());
            }

        } else {
            System.out.println("not connected");
        }
        g.close();
        System.out.println("end");
    }
}
