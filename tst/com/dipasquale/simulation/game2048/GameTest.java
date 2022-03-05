package com.dipasquale.simulation.game2048;

import com.dipasquale.common.random.float1.DeterministicRandomSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

public final class GameTest {
    @Test
    public void TEST_1() {
        List<Integer> actionIds = List.of(
                0, 3, 2, 1, 0, 3, 1, 2, 3,
                0, 1, 3, 0, 3, 2, 1, 0, 3,
                2, 0, 3, 0, 1, 2, 3, 2, 1,
                1, 0, 3, 1, 0, 3, 0, 1, 0,
                1
        );

        DeterministicRandomSupport locationRandomSupport = new DeterministicRandomSupport(16);
        DeterministicRandomSupport valueRandomSupport = new DeterministicRandomSupport(10);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        Game game = new Game(valuedTileSupport, 64);
        Iterator<Integer> actionIdsIterator = actionIds.iterator();
        RandomOutcomePlayer player = new RandomOutcomePlayer(state -> actionIdsIterator.next());
        GameResult result = game.play(player);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(320, result.getScore());
        Assertions.assertEquals(37, result.getMoveCount());
    }
}
