package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public final class TopologyMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = -1203469875972822936L;
    private final MetricDatum hiddenNodeGenes;
    private final MetricDatum connectionGenes;

    public static TopologyMetrics create(final MetricDatumFactory metricDatumFactory) {
        return new TopologyMetrics(metricDatumFactory.create(), metricDatumFactory.create());
    }

    public void merge(final TopologyMetrics topologyMetrics) {
        hiddenNodeGenes.merge(topologyMetrics.hiddenNodeGenes);
        connectionGenes.merge(topologyMetrics.connectionGenes);
    }

    public TopologyMetrics createCopy() {
        MetricDatum copiedHiddenNodeGenes = hiddenNodeGenes.createCopy();
        MetricDatum copiedConnectionGenes = connectionGenes.createCopy();

        return new TopologyMetrics(copiedHiddenNodeGenes, copiedConnectionGenes);
    }

    public void clear() {
        hiddenNodeGenes.clear();
        connectionGenes.clear();
    }
}
