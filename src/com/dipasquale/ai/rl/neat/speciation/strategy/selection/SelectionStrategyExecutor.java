package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class SelectionStrategyExecutor implements Serializable {
    @Serial
    private static final long serialVersionUID = -683595110196707789L;
    private final Collection<SelectionStrategy> strategies;

    public void select(final SelectionContext context, final NodeDeque<Species, StandardNode<Species>> speciesNodes) {
        Context.MetricsSupport metricsSupport = context.getParent().metrics();

        for (StandardNode<Species> speciesNode = speciesNodes.peekFirst(); speciesNode != null; ) {
            Species species = speciesNodes.getValue(speciesNode);
            boolean survives = species.shouldSurvive();

            for (SelectionStrategy strategy : strategies) {
                if (survives) {
                    strategy.prepareSurvival(context, species);
                } else {
                    strategy.prepareExtinction(context, species);
                }
            }

            metricsSupport.collectExtinction(species, !survives);

            if (!survives) {
                StandardNode<Species> speciesNodeNext = speciesNodes.peekNext(speciesNode);

                speciesNodes.remove(speciesNode);
                speciesNode = speciesNodeNext;
            } else {
                speciesNode = speciesNodes.peekNext(speciesNode);
            }
        }

        for (SelectionStrategy strategy : strategies) {
            strategy.finalizeSelection(context);
        }
    }
}
