package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.common.random.float2.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.MultiSearchNodeInitializer;
import com.dipasquale.search.mcts.SearchNodeCache;
import com.dipasquale.search.mcts.SearchNodeInitializer;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSearchNodeInitializerFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<SearchNodeInitializer<TAction, AlphaZeroEdge, TState>> {
    private final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoiseSettings;
    private final SearchNodeCache<TAction, AlphaZeroEdge, TState> nodeCache;

    private void addExplorationProbabilityNoiseIfApplicable(final List<SearchNodeInitializer<TAction, AlphaZeroEdge, TState>> nodeInitializers) {
        if (rootExplorationProbabilityNoiseSettings == null) {
            return;
        }

        float epsilon = rootExplorationProbabilityNoiseSettings.getEpsilon();

        if (Float.compare(epsilon, 0f) <= 0 || Float.compare(epsilon, 1f) > 0) {
            return;
        }

        double shape = rootExplorationProbabilityNoiseSettings.getShape();
        UniformRandomSupport uniformRandomSupport = new UniformRandomSupport();
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport();
        GammaDistributionSupport gammaDistributionSupport = new GammaDistributionSupport(uniformRandomSupport, gaussianDistributionSupport);
        DirichletDistributionSupport dirichletDistributionSupport = new DirichletDistributionSupport(gammaDistributionSupport);

        nodeInitializers.add(new ExplorationProbabilityNoiseRootSearchNodeInitializer<>(shape, dirichletDistributionSupport, epsilon));
    }

    private void addCacheIfApplicable(final List<SearchNodeInitializer<TAction, AlphaZeroEdge, TState>> nodeInitializers) {
        if (nodeCache == null) {
            return;
        }

        nodeInitializers.add(nodeCache::storeIfApplicable);
    }

    @Override
    public SearchNodeInitializer<TAction, AlphaZeroEdge, TState> create() {
        List<SearchNodeInitializer<TAction, AlphaZeroEdge, TState>> nodeInitializers = new ArrayList<>();

        addExplorationProbabilityNoiseIfApplicable(nodeInitializers);
        addCacheIfApplicable(nodeInitializers);

        return switch (nodeInitializers.size()) {
            case 0 -> null;

            case 1 -> nodeInitializers.get(0);

            default -> new MultiSearchNodeInitializer<>(nodeInitializers);
        };
    }
}
