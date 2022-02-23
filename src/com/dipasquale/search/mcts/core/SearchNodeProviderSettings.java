package com.dipasquale.search.mcts.core;

import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.common.random.float2.UniformRandomSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroExplorationProbabilityPerturber;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SearchNodeProviderSettings {
    private final boolean allowRootExplorationNoise;
    private final SearchNodeCacheSettings cache;

    <TAction extends Action, TState extends State<TAction, TState>> SearchNodeProvider<TAction, AlphaZeroEdge, TState> create(final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        List<SearchNodeProvider<TAction, AlphaZeroEdge, TState>> searchNodeProviders = new ArrayList<>();

        if (allowRootExplorationNoise) {
            UniformRandomSupport uniformRandomSupport = new UniformRandomSupport();
            GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport();
            GammaDistributionSupport gammaDistributionSupport = new GammaDistributionSupport(uniformRandomSupport, gaussianDistributionSupport);
            DirichletDistributionSupport dirichletDistributionSupport = new DirichletDistributionSupport(gammaDistributionSupport);

            searchNodeProviders.add(new AlphaZeroExplorationProbabilityPerturber<>(dirichletDistributionSupport));
        }

        if (cache != null) {
            searchNodeProviders.add(cache.create(edgeFactory));
        }

        int size = searchNodeProviders.size();

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return searchNodeProviders.get(0);
        }

        return new MultiSearchNodeProvider<>(searchNodeProviders);
    }
}
