package com.dipasquale.search.mcts.alphazero;

public interface AlphaZeroPrediction {
    float getPolicy(int index);

    float getValue();
}
