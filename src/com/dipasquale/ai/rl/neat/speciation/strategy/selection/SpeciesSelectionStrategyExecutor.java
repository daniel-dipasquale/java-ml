package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class SpeciesSelectionStrategyExecutor implements Serializable {
    @Serial
    private static final long serialVersionUID = -683595110196707789L;
    private final Collection<SpeciesSelectionStrategy> strategies;

    public void select(final SpeciesSelectionContext context, final NodeDeque<Species, SimpleNode<Species>> speciesNodes) {
        Context.MetricSupport metricSupport = context.getParent().metrics();

        for (SimpleNode<Species> speciesNode = speciesNodes.peekFirst(); speciesNode != null; ) {
            Species species = speciesNodes.getValue(speciesNode);
            boolean survives = species.shouldSurvive();

            for (SpeciesSelectionStrategy strategy : strategies) {
                if (survives) {
                    strategy.prepareSurvival(context, species);
                } else {
                    strategy.prepareExtinction(context, species);
                }
            }

            metricSupport.collectExtinction(species, !survives);

            if (!survives) {
                SimpleNode<Species> speciesNodeNext = speciesNodes.peekNext(speciesNode);

                speciesNodes.remove(speciesNode);
                speciesNode = speciesNodeNext;
            } else {
                speciesNode = speciesNodes.peekNext(speciesNode);
            }
        }

        for (SpeciesSelectionStrategy strategy : strategies) {
            strategy.finalizeSelection(context);
        }
    }
}
