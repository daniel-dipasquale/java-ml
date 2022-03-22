package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.common.random.float2.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Cache;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.MultiExpander;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroAdditionalExpanderFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<Expander<TAction, AlphaZeroEdge, TState>> {
    private final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoiseSettings;
    private final Cache<TAction, AlphaZeroEdge, TState> cache;

    private void addRootExplorationProbabilityNoiseIfApplicable(final List<Expander<TAction, AlphaZeroEdge, TState>> expanders) {
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

        expanders.add(new ExplorationProbabilityNoiseRootExpander<>(shape, dirichletDistributionSupport, epsilon));
    }

    private void addCacheIfApplicable(final List<Expander<TAction, AlphaZeroEdge, TState>> expanders) {
        if (cache == null) {
            return;
        }

        expanders.add(cache::storeIfApplicable);
    }

    @Override
    public Expander<TAction, AlphaZeroEdge, TState> create() {
        List<Expander<TAction, AlphaZeroEdge, TState>> expanders = new ArrayList<>();

        addRootExplorationProbabilityNoiseIfApplicable(expanders);
        addCacheIfApplicable(expanders);

        return switch (expanders.size()) {
            case 0 -> null;

            case 1 -> expanders.get(0);

            default -> new MultiExpander<>(expanders);
        };
    }
}
