package com.dipasquale.simulation.game2048;


import com.dipasquale.common.bit.int2.BitManipulatorSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.PrintStream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class Board {
    static final int DIMENSION = 4;
    static final int LENGTH = DIMENSION * DIMENSION;
    private static final long STARTING_STATE = 0L;
    private static final int STARTING_VALUED_TILE_COUNT = 0;
    private static final int STARTING_ACTION_IDS_ALLOWED = 0;
    private static final int STARTING_MAXIMUM_VALUE = 0;
    private static final int STARTING_SCORE = 0;
    static final int MAXIMUM_EXPONENT_PER_TILE = 16;
    private static final float PROBABILITY_OF_SPAWNING_2 = 0.9f;
    private static final int MAXIMUM_BITS_PER_TILE = (int) (Math.log(MAXIMUM_EXPONENT_PER_TILE) / Math.log(2D));
    private static final BitManipulatorSupport STATE_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    private static final int MAXIMUM_BITS_PER_ACTION_ID = 2;
    private static final com.dipasquale.common.bit.int1.BitManipulatorSupport ACTION_IDS_ALLOWED_BIT_MANIPULATOR_SUPPORT = com.dipasquale.common.bit.int1.BitManipulatorSupport.create(MAXIMUM_BITS_PER_ACTION_ID);
    static final int EMPTY_TILE_VALUE = 0;
    private ValuedTile lastValuedTile;
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private long state;
    private int valuedTileCount;
    private int actionIdsAllowed;
    private int maximumValue;
    private int score;

    Board() {
        this(null, STARTING_STATE, STARTING_VALUED_TILE_COUNT, STARTING_ACTION_IDS_ALLOWED, STARTING_MAXIMUM_VALUE, STARTING_SCORE);
    }

    public boolean isTemplate() {
        return lastValuedTile == null;
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

    private static int determineFromStateActionIdsAllowed(final long state) {
        int actionIdsAllowed = 0;

        for (ActionIdType actionIdType : ActionIdType.values()) {
            if (shouldActionIdBeAllowedGivenState(state, actionIdType)) {
                actionIdsAllowed = ACTION_IDS_ALLOWED_BIT_MANIPULATOR_SUPPORT.merge(actionIdsAllowed, actionIdType.getValue(), 1);
            }
        }

        return actionIdsAllowed;
    }

    public void initialize(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        long initialState = mergeValueToState(STARTING_STATE, valuedTile1);

        initialState = mergeValueToState(initialState, valuedTile2);
        state = initialState;
        lastValuedTile = valuedTile2;
        valuedTileCount = GameState.STARTING_VALUED_TILE_COUNT;
        actionIdsAllowed = determineFromStateActionIdsAllowed(initialState);
        maximumValue = Math.max(valuedTile1.getValue(), valuedTile2.getValue());
        score = 0;
    }

    private static int generateValue(final ValuedTileSupport valuedTileSupport) {
        return valuedTileSupport.generateValue(PROBABILITY_OF_SPAWNING_2);
    }

    public void initialize(final ValuedTileSupport valuedTileSupport) {
        int tileId1 = valuedTileSupport.generateId(0, LENGTH - 1);
        int tileId2 = valuedTileSupport.generateId(0, LENGTH);
        ValuedTile valuedTile1 = new ValuedTile(tileId1, generateValue(valuedTileSupport));
        ValuedTile valuedTile2;

        if (tileId1 == tileId2) {
            valuedTile2 = new ValuedTile(tileId2 + 1, generateValue(valuedTileSupport));
        } else {
            valuedTile2 = new ValuedTile(tileId2, generateValue(valuedTileSupport));
        }

        initialize(valuedTile1, valuedTile2);
    }

    private static ValuedTile generateValuedTile(final ValuedTileSupport valuedTileSupport, final long state, final int valuedTileCount) {
        int tileIdLogical = valuedTileSupport.generateId(0, LENGTH - valuedTileCount);
        int tileId = -1;

        for (int i1 = 0, i2 = 0; tileId == -1; i1++) {
            if (extractValueFromState(state, i1) == EMPTY_TILE_VALUE) {
                if (i2++ == tileIdLogical) {
                    tileId = i1;
                }
            }
        }

        int value = generateValue(valuedTileSupport);

        return new ValuedTile(tileId, value);
    }

    private Board createNext(final ActionIdType actionIdType, final ValuedTileGenerator valuedTileGenerator) {
        long originalState = state;
        long nextState = STARTING_STATE;
        int nextValuedTileCount = 0;
        int nextMaximumValue = maximumValue;
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
                        nextMaximumValue = Math.max(nextMaximumValue, newValue);
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

        ValuedTile nextLastValuedTile = null;
        int nextActionIdsAllowed = 0;

        if (valuedTileGenerator != null) {
            nextLastValuedTile = valuedTileGenerator.generate(nextState, nextValuedTileCount);
            nextState = mergeValueToState(nextState, nextLastValuedTile);
            nextValuedTileCount++;
            nextActionIdsAllowed = determineFromStateActionIdsAllowed(nextState);
            nextMaximumValue = Math.max(nextMaximumValue, nextLastValuedTile.getValue());
        }

        return new Board(nextLastValuedTile, nextState, nextValuedTileCount, nextActionIdsAllowed, nextMaximumValue, nextScore);
    }

    public Board generateNext(final ActionIdType actionIdType, final ValuedTileSupport valuedTileSupport) {
        ValuedTileGenerator valuedTileGenerator = (nextState, nextValuedTileCount) -> generateValuedTile(valuedTileSupport, nextState, nextValuedTileCount);

        return createNext(actionIdType, valuedTileGenerator);
    }

    public Board createNext(final ActionIdType actionIdType, final ValuedTile valuedTile) {
        ValuedTileGenerator valuedTileGenerator = (nextState, nextValuedTileCount) -> valuedTile;

        return createNext(actionIdType, valuedTileGenerator);
    }

    public Board createNextIfTileIsFree(final ActionIdType actionIdType, final ValuedTile valuedTile) {
        ValuedTileGenerator valuedTileGenerator = (nextState, nextValuedTileCount) -> {
            if (extractValueFromState(state, valuedTile.getId()) == EMPTY_TILE_VALUE) {
                return valuedTile;
            }

            throw new TileInitializedException(valuedTile.getValue());
        };

        return createNext(actionIdType, valuedTileGenerator);
    }

    public Board createNextTemplate(final ActionIdType actionIdType) {
        return createNext(actionIdType, (ValuedTileGenerator) null);
    }

    public Board createFromTemplate(final ValuedTile valuedTile) {
        long nextState = mergeValueToState(state, valuedTile);
        int nextValuedTileCount = valuedTileCount + 1;
        int nextActionIdsAllowed = determineFromStateActionIdsAllowed(nextState);
        int nextMaximumValue = Math.max(maximumValue, valuedTile.getValue());

        return new Board(valuedTile, nextState, nextValuedTileCount, nextActionIdsAllowed, nextMaximumValue, score);
    }

    public Board generateFromTemplate(final ValuedTileSupport valuedTileSupport) {
        ValuedTile valuedTile = generateValuedTile(valuedTileSupport, state, valuedTileCount);

        return createFromTemplate(valuedTile);
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

    public void print(final PrintStream stream) {
        stream.println("=============================");
        stream.printf("actions allowed: %s%n", createActionIdsAllowedText(actionIdsAllowed));
        stream.printf("maximum value: %s%n", (int) Math.pow(2D, maximumValue));
        stream.printf("score: %s%n", score);

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

    @FunctionalInterface
    interface ValuedTileGenerator {
        ValuedTile generate(long nextState, int nextValuedTileCount);
    }
}
