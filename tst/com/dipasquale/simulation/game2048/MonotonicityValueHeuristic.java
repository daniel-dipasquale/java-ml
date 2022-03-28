package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonotonicityValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final float MAXIMUM = (float) (Game.BOARD_ONE_DIMENSION_LENGTH * 2);
    private static final MonotonicityValueHeuristic INSTANCE = new MonotonicityValueHeuristic();

    public static MonotonicityValueHeuristic getInstance() {
        return INSTANCE;
    }

    private static int getHorizontalTileId(final int row, final int cell) {
        return Game.BOARD_ONE_DIMENSION_LENGTH * row + cell;
    }

    private static int getHorizontalValue(final GameState state, final int row, final int cell) {
        int tileId = getHorizontalTileId(row, cell);

        return state.getValueInTile(tileId);
    }

    private static boolean isRowMonotonous(final GameState state, final int row) {
        boolean monotonous = true;
        int value = getHorizontalValue(state, row, 0);

        for (int i = 1; monotonous && i < Game.BOARD_ONE_DIMENSION_LENGTH; i++) {
            int nextValue = getHorizontalValue(state, row, i);

            if (nextValue != 0) {
                monotonous = value >= nextValue;
                value = nextValue;
            }
        }

        return monotonous;
    }

    private static int getVerticalTileId(final int column, final int cell) {
        return column + Game.BOARD_ONE_DIMENSION_LENGTH * cell;
    }

    private static int getVerticalValue(final GameState state, final int column, final int cell) {
        int tileId = getVerticalTileId(column, cell);

        return state.getValueInTile(tileId);
    }

    private static boolean isColumnMonotonous(final GameState state, final int column) {
        boolean monotonous = true;
        int value = getVerticalValue(state, column, 0);

        for (int i = 1; monotonous && i < Game.BOARD_ONE_DIMENSION_LENGTH; i++) {
            int nextValue = getVerticalValue(state, column, i);

            if (nextValue != 0) {
                monotonous = value >= nextValue;
                value = nextValue;
            }
        }

        return monotonous;
    }

    @Override
    public float estimate(final GameState state) {
        int count = 0;

        for (int i = 0; i < Game.BOARD_ONE_DIMENSION_LENGTH; i++) {
            if (isRowMonotonous(state, i)) {
                count++;
            }

            if (isColumnMonotonous(state, i)) {
                count++;
            }
        }

        float rate = (float) count / MAXIMUM;

        return ValueHeuristic.convertProbability(rate);
    }
}
