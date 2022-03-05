package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class TwoPlayerP55WinRateTrainingAssessor<T> implements NeatTrainingAssessor {
    @Serial
    private static final long serialVersionUID = -3994281075074025308L;
    private final TwoPlayerGameSupport<T> twoPlayerGameSupport;
    private final int matches;

    @Override
    public boolean test(final NeatActivator activator) {
        T aiPlayer = twoPlayerGameSupport.createPlayer(activator);
        T basicPlayer = twoPlayerGameSupport.createBasicPlayer();
        int won = 0;
        int expectedWins = (int) Math.ceil((double) matches * 0.55D);

        for (int i = 0; won < expectedWins && i - won < expectedWins; i++) {
            if (i % 2 == 0) {
                if (twoPlayerGameSupport.play(aiPlayer, basicPlayer) == 0) {
                    won++;
                }
            } else if (twoPlayerGameSupport.play(basicPlayer, aiPlayer) == 1) {
                won++;
            }
        }

        return won == expectedWins;
    }
}
