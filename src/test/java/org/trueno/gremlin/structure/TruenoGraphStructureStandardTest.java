package org.trueno.gremlin.structure;

import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.structure.StructureStandardSuite;

import org.trueno.gremlin.TruenoGraphProvider;

import org.junit.runner.RunWith;

/**
 * Structure API Tests
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
@RunWith(StructureStandardSuite.class)
@GraphProviderClass(provider = TruenoGraphProvider.class, graph = TruenoGraph.class)
public class TruenoGraphStructureStandardTest {
}
