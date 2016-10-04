import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.trueno.structure.TruenoGraph;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class GraphTest {

    public static void main(String[] args) throws InterruptedException {
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

            g.addVertex(T.label, "one");
            g.addVertex(T.label, "two");
            g.addVertex(T.label, "three");
            g.addVertex();
            g.vertices();

        } else {
            System.out.println("not connected");
        }
        System.out.println("end");
    }
}
