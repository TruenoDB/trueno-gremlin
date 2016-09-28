package org.apache.tinkerpop.gremlin.trueno.groovy.plugin;

import org.apache.tinkerpop.gremlin.groovy.plugin.*;
import org.apache.tinkerpop.gremlin.trueno.structure.TruenoGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public final class TruenoGremlinPlugin extends AbstractGremlinPlugin {

    private static final Set<String> IMPORTS = new HashSet<String>() {{
        add(IMPORT_SPACE + TruenoGraph.class.getPackage().getName() + DOT_STAR);
    }};

    @Override
    public String getName() {
        return "trueno";
    }

    @Override
    public void pluginTo(PluginAcceptor pluginAcceptor) throws IllegalEnvironmentException, PluginInitializationException {
        pluginAcceptor.addImports(IMPORTS);
    }

    @Override
    public void afterPluginTo(final PluginAcceptor pluginAcceptor) throws IllegalEnvironmentException, PluginInitializationException {

    }

    @Override
    public boolean requireRestart() {
        return true;
    }
}
