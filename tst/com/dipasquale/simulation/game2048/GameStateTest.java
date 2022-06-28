package com.dipasquale.simulation.game2048;

import com.dipasquale.data.structure.collection.ListSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

public final class GameStateTest {
    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "initialActionBuilder")
    private static GameAction createInitialAction(final int actionId, final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        return new GameAction(actionId, List.of(valuedTile1, valuedTile2));
    }

    private static GameAction createValuedTileAllocationAction(final int actionId, final ValuedTile valuedTile) {
        return new GameAction(actionId, List.of(valuedTile));
    }

    private static TreeMap<Integer, Integer> createInitialTileIds1() {
        TreeMap<Integer, Integer> tileIds = new TreeMap<>(Integer::compareTo);

        for (int size = GameState.BOARD_LENGTH - 1, sum = 0, i = size; i > 0; i--) {
            sum += i * 4;
            tileIds.put(sum, size - i);
        }

        return tileIds;
    }

    private static TreeMap<Integer, Integer> createInitialTileIds2() {
        TreeMap<Integer, Integer> tileIds = new TreeMap<>(Integer::compareTo);

        for (int size = GameState.BOARD_LENGTH - 1, sum = 0, i1 = size; i1 > 0; i1--) {
            for (int i2 = 0; i2 < i1; i2++) {
                sum += 4;
                tileIds.put(sum, size - i1 + i2 + 1);
            }
        }

        return tileIds;
    }

    @Test
    public void TEST_1() {
        GameState test = new GameState(5);

        Assertions.assertEquals(0, test.getDepth());
        Assertions.assertEquals(MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID, test.getStatusId());
        Assertions.assertEquals(GameState.INTENTIONAL_PLAYER_ID, test.getParticipantId());
        Assertions.assertTrue(test.isActionIntentional());
        Assertions.assertEquals(GameState.UNINTENTIONAL_PLAYER_ID, test.getNextParticipantId());
        Assertions.assertFalse(test.isNextActionIntentional());
        Assertions.assertEquals(GameState.ROOT_ACTION, test.getLastAction());

        int size = GameState.BOARD_LENGTH * (GameState.BOARD_LENGTH - 1) * 2;
        TreeMap<Integer, Integer> tileIds1 = createInitialTileIds1();
        int[] tileValues1 = {1, 1, 2, 2};
        TreeMap<Integer, Integer> tileIds2 = createInitialTileIds2();
        List<GameAction> result = ListSupport.copyOf(test.createAllPossibleActions());

        Assertions.assertEquals(IntStream.range(0, size)
                .mapToObj(index -> initialActionBuilder()
                        .actionId(index)
                        .valuedTile1(new ValuedTile(tileIds1.higherEntry(index).getValue(), tileValues1[index % 4]))
                        .valuedTile2(new ValuedTile(tileIds2.higherEntry(index).getValue(), index % 2 + 1))
                        .build())
                .toList(), result);
    }

    @Test
    public void TEST_2() {
        GameState state = new GameState(5);
        GameAction action = state.createInitialAction(new ValuedTile(0, 1), new ValuedTile(5, 2));
        GameState test = state.accept(action);

        Assertions.assertEquals(1, test.getDepth());
        Assertions.assertEquals(MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID, test.getStatusId());
        Assertions.assertEquals(GameState.UNINTENTIONAL_PLAYER_ID, test.getParticipantId());
        Assertions.assertFalse(test.isActionIntentional());
        Assertions.assertEquals(GameState.INTENTIONAL_PLAYER_ID, test.getNextParticipantId());
        Assertions.assertTrue(test.isNextActionIntentional());

        Assertions.assertEquals(initialActionBuilder()
                .actionId(17)
                .valuedTile1(new ValuedTile(0, 1))
                .valuedTile2(new ValuedTile(5, 2))
                .build(), test.getLastAction());

        List<GameAction> result = ListSupport.copyOf(test.createAllPossibleActions());

        Assertions.assertEquals(ListSupport.builder()
                .add(GameAction.createIntentional(0))
                .add(GameAction.createIntentional(1))
                .add(GameAction.createIntentional(2))
                .add(GameAction.createIntentional(3))
                .build(), result);
    }

    @Test
    public void TEST_3() {
        GameState state1 = new GameState(5);
        GameAction action1 = state1.createInitialAction(new ValuedTile(0, 1), new ValuedTile(5, 2));
        GameState state2 = state1.accept(action1);
        GameAction action2 = state2.createAction(ActionIdType.LEFT);
        GameState test = state2.accept(action2);

        Assertions.assertEquals(2, test.getDepth());
        Assertions.assertEquals(MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID, test.getStatusId());
        Assertions.assertEquals(GameState.INTENTIONAL_PLAYER_ID, test.getParticipantId());
        Assertions.assertTrue(test.isActionIntentional());
        Assertions.assertEquals(GameState.UNINTENTIONAL_PLAYER_ID, test.getNextParticipantId());
        Assertions.assertFalse(test.isNextActionIntentional());
        Assertions.assertEquals(GameAction.createIntentional(ActionIdType.LEFT), test.getLastAction());

        List<GameAction> result = ListSupport.copyOf(test.createAllPossibleActions());

        Assertions.assertEquals(ListSupport.builder()
                .add(createValuedTileAllocationAction(0, new ValuedTile(1, 1)))
                .add(createValuedTileAllocationAction(1, new ValuedTile(1, 2)))
                .add(createValuedTileAllocationAction(2, new ValuedTile(2, 1)))
                .add(createValuedTileAllocationAction(3, new ValuedTile(2, 2)))
                .add(createValuedTileAllocationAction(4, new ValuedTile(3, 1)))
                .add(createValuedTileAllocationAction(5, new ValuedTile(3, 2)))
                .add(createValuedTileAllocationAction(6, new ValuedTile(5, 1)))
                .add(createValuedTileAllocationAction(7, new ValuedTile(5, 2)))
                .add(createValuedTileAllocationAction(8, new ValuedTile(6, 1)))
                .add(createValuedTileAllocationAction(9, new ValuedTile(6, 2)))
                .add(createValuedTileAllocationAction(10, new ValuedTile(7, 1)))
                .add(createValuedTileAllocationAction(11, new ValuedTile(7, 2)))
                .add(createValuedTileAllocationAction(12, new ValuedTile(8, 1)))
                .add(createValuedTileAllocationAction(13, new ValuedTile(8, 2)))
                .add(createValuedTileAllocationAction(14, new ValuedTile(9, 1)))
                .add(createValuedTileAllocationAction(15, new ValuedTile(9, 2)))
                .add(createValuedTileAllocationAction(16, new ValuedTile(10, 1)))
                .add(createValuedTileAllocationAction(17, new ValuedTile(10, 2)))
                .add(createValuedTileAllocationAction(18, new ValuedTile(11, 1)))
                .add(createValuedTileAllocationAction(19, new ValuedTile(11, 2)))
                .add(createValuedTileAllocationAction(20, new ValuedTile(12, 1)))
                .add(createValuedTileAllocationAction(21, new ValuedTile(12, 2)))
                .add(createValuedTileAllocationAction(22, new ValuedTile(13, 1)))
                .add(createValuedTileAllocationAction(23, new ValuedTile(13, 2)))
                .add(createValuedTileAllocationAction(24, new ValuedTile(14, 1)))
                .add(createValuedTileAllocationAction(25, new ValuedTile(14, 2)))
                .add(createValuedTileAllocationAction(26, new ValuedTile(15, 1)))
                .add(createValuedTileAllocationAction(27, new ValuedTile(15, 2)))
                .build(), result);
    }
}
