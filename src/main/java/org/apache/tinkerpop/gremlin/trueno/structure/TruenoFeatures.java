package org.apache.tinkerpop.gremlin.trueno.structure;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

/**
 * A class that represent the capabilities supported by Trueno Graph. By default all methods of features in
 * Graph.Features {@link org.apache.tinkerpop.gremlin.structure.Graph.Features} return true. Thus, the implementation
 * only overrides the features not supported.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
    public class TruenoFeatures implements Graph.Features {

        protected GraphFeatures graphFeatures = new TruenoGraphFeatures();
        protected VertexFeatures vertexFeatures = new TruenoVertexFeatures();
        protected EdgeFeatures edgesFeatures = new TruenoEdgeFeatures();

        /**
         * Features specific to a operations of a Trueno Graph
         * {@link org.apache.tinkerpop.gremlin.trueno.structure.TruenoGraph}.
         */
        public class TruenoGraphFeatures implements GraphFeatures {

            private VariableFeatures variableFeatures = new TruenoVariableFeatures();

            TruenoGraphFeatures() {
            }

            @Override
            public boolean supportsThreadedTransactions() {
                return false;
            }

            @Override
            public boolean supportsComputer() {
                return false;
            }

            @Override
            public boolean supportsConcurrentAccess() {
                return false;
            }

            @Override
            public boolean supportsTransactions() {
                return false;
            }

            @Override
            public VariableFeatures variables() {
                return variableFeatures;
            }
        }

        /**
         * Features that are related to Vertex {@link org.apache.tinkerpop.gremlin.trueno.structure.TruenoVertex}
         * operations and supported by Trueno.
         */
        public class TruenoVertexFeatures implements VertexFeatures {

            private final VertexPropertyFeatures vertexPropertyFeatures = new TruenoVertexPropertyFeatures();

            TruenoVertexFeatures(){
            }

            @Override
            public VertexPropertyFeatures properties() {
                return this.vertexPropertyFeatures;
            }

            @Override
            public VertexProperty.Cardinality getCardinality(String key) {
                return VertexProperty.Cardinality.single;
            }

            // FIXME: Trueno actually supports meta properties.
            @Override
            public boolean supportsMetaProperties() {
                return false;
            }

            @Override
            public boolean supportsMultiProperties() {
                return false;
            }

            @Override
            public boolean supportsUserSuppliedIds() {
                return false;
            }

        }

        /**
         * Features that are related to Edges operations and supported by Trueno.
         */
        public class TruenoEdgeFeatures implements EdgeFeatures {

            private final EdgePropertyFeatures edgePropertyFeatures = new TruenoEdgePropertyFeatures();

            TruenoEdgeFeatures() {
            }

            @Override
            public EdgePropertyFeatures properties() {
                return this.edgePropertyFeatures;
            }
        }

        /**
         * Features for Graph.Variables and supported by Trueno.
         */
        public class TruenoVariableFeatures implements VariableFeatures {

            @Override
            public boolean supportsByteValues() {
                return false;
            }

            @Override
            public boolean supportsMapValues() {
                return false;
            }

            @Override
            public boolean supportsMixedListValues() {
                return false;
            }

            @Override
            public boolean supportsBooleanArrayValues() {
                return false;
            }

            @Override
            public boolean supportsByteArrayValues() {
                return false;
            }

            @Override
            public boolean supportsDoubleArrayValues() {
                return false;
            }

            @Override
            public boolean supportsFloatArrayValues() {
                return false;
            }

            @Override
            public boolean supportsIntegerArrayValues() {
                return false;
            }

            @Override
            public boolean supportsStringArrayValues() {
                return false;
            }

            @Override
            public boolean supportsLongArrayValues() {
                return false;
            }

            @Override
            public boolean supportsSerializableValues() {
                return false;
            }

            @Override
            public boolean supportsStringValues() {
                return false;
            }

            @Override
            public boolean supportsUniformListValues() {
                return false;
            }
        }
        /**
         * Features that are related to Vertex Property objects and supported by Trueno.
         */
        public class TruenoVertexPropertyFeatures implements VertexPropertyFeatures {

            TruenoVertexPropertyFeatures() {
            }

            @Override
            public boolean supportsUserSuppliedIds() {
                return true;
            }

            @Override
            public boolean supportsAnyIds() {
                return false;
            }

            @Override
            public boolean supportsMapValues() {
                return false;
            }

            @Override
            public boolean supportsMixedListValues() {
                return false;
            }

            @Override
            public boolean supportsSerializableValues() {
                return false;
            }

            @Override
            public boolean supportsUniformListValues() {
                return false;
            }
        }

        /**
         * Features that are related to Edge Property objects and supported by Trueno.
         */
        public class TruenoEdgePropertyFeatures implements  EdgePropertyFeatures {

            TruenoEdgePropertyFeatures() {
            }

            @Override
            public boolean supportsMapValues() {
                return false;
            }

            @Override
            public boolean supportsMixedListValues() {
                return false;
            }

            @Override
            public boolean supportsSerializableValues() {
                return false;
            }

            @Override
            public boolean supportsUniformListValues() {
                return false;
            }
        }

    }
