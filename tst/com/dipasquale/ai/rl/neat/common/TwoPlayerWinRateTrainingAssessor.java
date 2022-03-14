package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class TwoPlayerWinRateTrainingAssessor<T> implements NeatTrainingAssessor {
    @Serial
    private static final long serialVersionUID = -3994281075074025308L;
    private final TwoPlayerGameSupport<T> twoPlayerGameSupport;
    private final int matches;
    private final double winRate;

    @Override
    public boolean test(final NeatActivator activator) {
        T aiPlayer = twoPlayerGameSupport.createPlayer(activator);
        T basicPlayer = twoPlayerGameSupport.createClassicPlayer();
        int won = 0;
        int expectedWins = Math.min((int) Math.round((double) matches * winRate), 1);

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
