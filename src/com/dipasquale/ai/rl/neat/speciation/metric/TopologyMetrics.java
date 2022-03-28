package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public final class TopologyMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = -1203469875972822936L;
    private final MetricDatum hiddenNodes;
    private final MetricDatum connections;

    public void merge(final TopologyMetrics topologyMetrics) {
        hiddenNodes.merge(topologyMetrics.hiddenNodes);
        connections.merge(topologyMetrics.connections);
    }

    public TopologyMetrics createCopy() {
        MetricDatum copiedHiddenNodes = hiddenNodes.createCopy();
        MetricDatum copiedConnections = connections.createCopy();

        return new TopologyMetrics(copiedHiddenNodes, copiedConnections);
    }

    public void clear() {
        hiddenNodes.clear();
        connections.clear();
    }
}
