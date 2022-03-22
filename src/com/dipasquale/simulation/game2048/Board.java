package com.dipasquale.simulation.game2048;


import com.dipasquale.common.bit.int2.BitManipulatorSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.PrintStream;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class Board {
    static final int DIMENSION = 4;
    static final int LENGTH = DIMENSION * DIMENSION;
    static final int MAXIMUM_EXPONENT_PER_TILE = 16;
    private static final int MAXIMUM_BITS_PER_TILE = (int) (Math.log(MAXIMUM_EXPONENT_PER_TILE) / Math.log(2D));
    private static final BitManipulatorSupport STATE_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    private static final int MAXIMUM_BITS_PER_ACTION_ID = 2;
    private static final com.dipasquale.common.bit.int1.BitManipulatorSupport ACTION_IDS_ALLOWED_BIT_MANIPULATOR_SUPPORT = com.dipasquale.common.bit.int1.BitManipulatorSupport.create(MAXIMUM_BITS_PER_ACTION_ID);
    static final List<ValuedTile> NO_VALUED_TILES = List.of();
    static final long INITIAL_STATE = 0L;
    private static final int INITIAL_VALUED_TILE_COUNT = 0;
    private static final int INITIAL_ACTION_IDS_ALLOWED = 0;
    private static final int INITIAL_HIGHEST_VALUE = 0;
    private static final int INITIAL_SCORE = 0;
    static final int EMPTY_TILE_VALUE = 0;
    private List<ValuedTile> valuedTilesAdded;
    @Getter(AccessLevel.NONE)
    private long state;
    private int valuedTileCount;
    private int actionIdsAllowed;
    private int highestValue;
    private int score;

    Board() {
        this(NO_VALUED_TILES, INITIAL_STATE, INITIAL_VALUED_TILE_COUNT, INITIAL_ACTION_IDS_ALLOWED, INITIAL_HIGHEST_VALUE, INITIAL_SCORE);
    }

    public boolean isPartiallyInitialized() {
        return valuedTilesAdded.isEmpty();
    }

    private static int extractValueFromState(final long state, final int tileId) {
        return (int) STATE_MANIPULATOR_SUPPORT.extract(state, tileId);
    }

    public int getValue(final int tileId) {
        return extractValueFromState(state, tileId);
    }

    private static boolean isActionIdAllowed(final int actionIdsAllowed, final int actionId) {
        return ACTION_IDS_ALLOWED_BIT_MANIPULATOR_SUPPORT.extract(actionIdsAllowed, actionId) == 1;
    }

    public boolean isActionIdValid(final int id) {
        return isActionIdAllowed(actionIdsAllowed, id);
    }

    private static long mergeValueToState(final long state, final int tileId, final int value) {
        return STATE_MANIPULATOR_SUPPORT.merge(state, tileId, value);
    }

    private static int getTileId(final ActionIdType actionIdType, final int x, final int y) {
        return switch (actionIdType) {
            case LEFT -> DIMENSION * x + y;

            case UP -> DIMENSION * y + x;

            case RIGHT -> DIMENSION * x + (DIMENSION - y - 1);

            case DOWN -> DIMENSION * (DIMENSION - y - 1) + x;
        };
    }

    private static int extractValueFromState(final long state, final int x, final int y, final ActionIdType actionIdType) {
        int tileId = getTileId(actionIdType, x, y);

        return extractValueFromState(state, tileId);
    }

    private static long mergeValueToState(final long state, final int x, final int y, final int value, final ActionIdType actionIdType) {
        int tileId = getTileId(actionIdType, x, y);

        return mergeValueToState(state, tileId, value);
    }

    private static long mergeValueToState(final long state, final ValuedTile valuedTile) {
        return mergeValueToState(state, valuedTile.getId(), valuedTile.getValue());
    }

    private static boolean shouldActionIdBeAllowedGivenState(final long state, final ActionIdType actionIdType) {
        for (int x = 0; x < DIMENSION; x++) {
            int firstFreeTileIndex = Integer.MAX_VALUE;
            int lastValuedTileIndex = -1;

            for (int y = 0; y < DIMENSION; y++) {
                int value1 = extractValueFromState(state, x, y, actionIdType);

                if (value1 != EMPTY_TILE_VALUE) {
                    int value2 = EMPTY_TILE_VALUE;
                    int value2Index = y + 1;

                    if (value2Index < DIMENSION) {
                        do {
                            value2 = extractValueFromState(state, x, value2Index, actionIdType);

                            if (value2 == EMPTY_TILE_VALUE) {
                                firstFreeTileIndex = Math.min(firstFreeTileIndex, value2Index);
                            }

                            value2Index++;
                        } while (value2Index < DIMENSION && value2 == EMPTY_TILE_VALUE);
                    }

                    if (value1 == value2) {
                        return true;
                    }

                    lastValuedTileIndex = y;

                    if (value2 != EMPTY_TILE_VALUE) {
                        y = value2Index - 2;
                    } else {
                        y = value2Index;
                    }
                } else {
                    firstFreeTileIndex = Math.min(firstFreeTileIndex, y);
                }
            }

            if (firstFreeTileIndex < lastValuedTileIndex) {
                return true;
            }
        }

        return false;
    }

    private static int determineFromStateAllActionIdsAllowed(final long state) {
        int actionIdsAllowed = 0;

        for (ActionIdType actionIdType : ActionIdType.values()) {
            if (shouldActionIdBeAllowedGivenState(state, actionIdType)) {
                actionIdsAllowed = ACTION_IDS_ALLOWED_BIT_MANIPULATOR_SUPPORT.merge(actionIdsAllowed, actionIdType.getValue(), 1);
            }
        }

        return actionIdsAllowed;
    }

    public Board createNextPartiallyInitialized(final ActionIdType actionIdType) {
        long originalState = state;
        long nextState = INITIAL_STATE;
        int nextValuedTileCount = 0;
        int nextHighestValue = highestValue;
        int nextScore = score;

        for (int dimension = 0; dimension < DIMENSION; dimension++) {
            for (int readIndex = 0, writeIndex = 0; readIndex < DIMENSION; readIndex++) {
                int value1 = extractValueFromState(originalState, dimension, readIndex, actionIdType);

                if (value1 != EMPTY_TILE_VALUE) {
                    int value2 = EMPTY_TILE_VALUE;
                    int value2ReadIndex = readIndex + 1;

                    if (value2ReadIndex < DIMENSION) {
                        do {
                            value2 = extractValueFromState(originalState, dimension, value2ReadIndex, actionIdType);
                            value2ReadIndex++;
                        } while (value2ReadIndex < DIMENSION && value2 == EMPTY_TILE_VALUE);
                    }

                    if (value1 == value2) {
                        int newValue = value1 + 1;

                        nextState = mergeValueToState(nextState, dimension, writeIndex, newValue, actionIdType);
                        nextHighestValue = Math.max(nextHighestValue, newValue);
                        nextScore += (int) Math.pow(2D, newValue);
                        readIndex = value2ReadIndex - 1;
                    } else {
                        nextState = mergeValueToState(nextState, dimension, writeIndex, value1, actionIdType);

                        if (value2 != EMPTY_TILE_VALUE) {
                            readIndex = value2ReadIndex - 2;
                        } else {
                            readIndex = value2ReadIndex;
                        }
                    }

                    nextValuedTileCount++;
                    writeIndex++;
                }
            }
        }

        return new Board(NO_VALUED_TILES, nextState, nextValuedTileCount, INITIAL_ACTION_IDS_ALLOWED, nextHighestValue, nextScore);
    }

    public Board createNext(final List<ValuedTile> valuedTiles) {
        long nextState = state;
        int nextValuedTileCount = valuedTileCount;
        int nextHighestValue = highestValue;

        for (ValuedTile valuedTile : valuedTiles) {
            nextState = mergeValueToState(nextState, valuedTile);
            nextValuedTileCount++;
            nextHighestValue = Math.max(nextHighestValue, valuedTile.getValue());
        }

        int nextActionIdsAllowed = determineFromStateAllActionIdsAllowed(nextState);

        return new Board(valuedTiles, nextState, nextValuedTileCount, nextActionIdsAllowed, nextHighestValue, score);
    }

    private static String createActionIdsAllowedText(final int actionIdsAllowed) {
        StringBuilder actionIdsAllowedStringBuilder = new StringBuilder();

        if (isActionIdAllowed(actionIdsAllowed, ActionIdType.LEFT.getValue())) {
            actionIdsAllowedStringBuilder.append("L");
        } else {
            actionIdsAllowedStringBuilder.append("_");
        }

        if (isActionIdAllowed(actionIdsAllowed, ActionIdType.UP.getValue())) {
            actionIdsAllowedStringBuilder.append("U");
        } else {
            actionIdsAllowedStringBuilder.append("_");
        }

        if (isActionIdAllowed(actionIdsAllowed, ActionIdType.RIGHT.getValue())) {
            actionIdsAllowedStringBuilder.append("R");
        } else {
            actionIdsAllowedStringBuilder.append("_");
        }

        if (isActionIdAllowed(actionIdsAllowed, ActionIdType.DOWN.getValue())) {
            actionIdsAllowedStringBuilder.append("D");
        } else {
            actionIdsAllowedStringBuilder.append("_");
        }

        return actionIdsAllowedStringBuilder.toString();
    }

    public void print(final PrintStream stream, final int depth, final ActionIdType actionIdType) {
        stream.println("=============================");
        stream.printf("depth: %d%n", depth);
        stream.printf("actions allowed: %s%n", createActionIdsAllowedText(actionIdsAllowed));
        stream.printf("highest value: %s%n", (int) Math.pow(2D, highestValue));
        stream.printf("score: %s%n", score);

        if (actionIdType != null) {
            stream.printf("last action: %s%n", actionIdType);
        } else {
            stream.printf("last action: added tile%n");
        }

        for (int x = 0; x < Board.DIMENSION; x++) {
            stream.print("|");

            for (int y = 0; y < Board.DIMENSION; y++) {
                int tileId = getTileId(ActionIdType.LEFT, x, y);
                int value = (int) Math.pow(2D, extractValueFromState(state, tileId));

                if (value >= 2) {
                    stream.printf("%1$5s |", value);
                } else {
                    stream.print("      |");
                }
            }

            stream.println();
        }

        stream.println("=============================");
    }
}
