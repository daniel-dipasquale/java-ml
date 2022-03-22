package com.dipasquale.simulation.game2048;

import com.dipasquale.common.random.float1.DeterministicRandomSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class GameTest {
    @Test
    public void TEST_1() {
        List<Integer> actionIds = List.of(
                0, 3, 2, 1, 0, 3, 1, 2, 3,
                0, 1, 3, 0, 0, 3, 2, 0, 1,
                0, 0, 1, 2, 2, 1, 3, 0, 3,
                2, 3, 1, 0, 3, 1, 2, 1, 1,
                2, 2, 1
        );

        DeterministicRandomSupport locationRandomSupport = new DeterministicRandomSupport(16);
        DeterministicRandomSupport valueRandomSupport = new DeterministicRandomSupport(10);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        Game game = new Game(valuedTileSupport, 6, new RandomValuedTileAdderPlayer());
        ActionIdModelPlayer player = new ActionIdModelPlayer(new ListActionIdModel(actionIds));
        GameResult result = game.play(player);

        Assertions.assertTrue(result.isSuccessful());
        Assertions.assertEquals(332, result.getScore());
        Assertions.assertEquals(actionIds.size(), result.getMoveCount());
    }
}
