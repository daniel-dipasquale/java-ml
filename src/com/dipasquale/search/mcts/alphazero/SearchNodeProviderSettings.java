package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.common.random.float2.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeCacheSettings;
import com.dipasquale.search.mcts.SearchNodeProvider;
import com.dipasquale.search.mcts.State;
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

    public <TAction extends Action, TState extends State<TAction, TState>> SearchNodeProvider<TAction, AlphaZeroEdge, TState> create(final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        List<SearchNodeProvider<TAction, AlphaZeroEdge, TState>> nodeProviders = new ArrayList<>();

        if (allowRootExplorationNoise) {
            UniformRandomSupport uniformRandomSupport = new UniformRandomSupport();
            GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport();
            GammaDistributionSupport gammaDistributionSupport = new GammaDistributionSupport(uniformRandomSupport, gaussianDistributionSupport);
            DirichletDistributionSupport dirichletDistributionSupport = new DirichletDistributionSupport(gammaDistributionSupport);

            nodeProviders.add(new InitialExplorationProbabilityNoiseSearchNodeProvider<>(dirichletDistributionSupport));
        }

        if (cache != null) {
            nodeProviders.add(cache.create(edgeFactory));
        }

        int size = nodeProviders.size();

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return nodeProviders.get(0);
        }

        return new MultiSearchNodeProvider<>(nodeProviders);
    }
}
