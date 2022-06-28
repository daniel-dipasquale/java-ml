package com.dipasquale.simulation.game2048;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class Board {
    public static final int DIMENSION_VECTOR_LENGTH = ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH;
    public static final int VECTOR_LENGTH = ValuedTileSupport.BOARD_VECTOR_LENGTH;
    private static final long EMPTY_VECTOR = ValuedTileSupport.EMPTY_BOARD_VECTOR;
    private static final List<ValuedTile> EMPTY_VALUED_TILES_ADDED = ValuedTileSupport.EMPTY_VALUED_TILES;
    private static final int ZERO_VALUED_TILE_COUNT = 0;
    private static final long INITIAL_FREED_ID_TO_ID_MAPPING_VECTOR = ValuedTileSupport.INITIAL_FREED_ID_TO_ID_MAPPING_VECTOR;
    private static final int INITIAL_HIGHEST_EXPONENTIAL_VALUE = 0;
    private static final int INITIAL_SCORE = 0;
    @Getter(AccessLevel.NONE)
    private final long vector;
    private final List<ValuedTile> valuedTilesAdded;
    private final int valuedTileCount;
    private final long freedTileIdToTileIdMappingVector;
    private final long tileIdToFreedTileIdMappingVector;
    private final int intentionalActionIdsAllowedVector;
    private final int highestExponentialValue;
    private final int score;

    Board() {
        this(EMPTY_VECTOR, EMPTY_VALUED_TILES_ADDED, ZERO_VALUED_TILE_COUNT, INITIAL_FREED_ID_TO_ID_MAPPING_VECTOR, INITIAL_FREED_ID_TO_ID_MAPPING_VECTOR, ActionIdSupport.EMPTY_VECTOR, INITIAL_HIGHEST_EXPONENTIAL_VALUE, INITIAL_SCORE);
    }

    public int getExponentialValue(final int tileId) {
        return ValuedTileSupport.extractExponentialValue(vector, tileId);
    }

    public int getTileId(final int freedTileId) {
        return ValuedTileSupport.extractTileId(freedTileIdToTileIdMappingVector, freedTileId);
    }

    public int getFreedTileId(final int tileId) {
        return ValuedTileSupport.extractTileId(tileIdToFreedTileIdMappingVector, tileId) - 1;
    }

    public boolean isIntentionalActionIdValid(final int id) {
        return ActionIdSupport.isAllowed(intentionalActionIdsAllowedVector, id);
    }

    public Board createNext(final ActionIdType actionIdType) {
        long nextVector = EMPTY_VECTOR;
        int nextValuedTileCount = ZERO_VALUED_TILE_COUNT;
        int freedTileId = 0;
        long nextFreedTileIdToTileIdMappingVector = ValuedTileSupport.EMPTY_FREED_ID_TO_ID_MAPPING_VECTOR;
        long nextTileIdToFreedTileIdMappingVector = ValuedTileSupport.EMPTY_FREED_ID_TO_ID_MAPPING_VECTOR;
        int nextHighestExponentialValue = highestExponentialValue;
        int nextScore = score;

        for (int x = 0; x < DIMENSION_VECTOR_LENGTH; x++) {
            int lastWrittenY = -1;

            for (int readY = 0, writeY = 0; readY < DIMENSION_VECTOR_LENGTH; readY++) {
                int readTileId = ValuedTileSupport.calculateTileId(actionIdType, x, readY);
                int exponentialValue = ValuedTileSupport.extractExponentialValue(vector, readTileId);

                if (exponentialValue != ValuedTileSupport.EMPTY_TILE_VALUE) {
                    int lookAheadY = readY + 1;
                    int lookAheadExponentialValue = ValuedTileSupport.EMPTY_TILE_VALUE;

                    if (lookAheadY < DIMENSION_VECTOR_LENGTH) {
                        do {
                            int lookAheadTileId = ValuedTileSupport.calculateTileId(actionIdType, x, lookAheadY);

                            lookAheadExponentialValue = ValuedTileSupport.extractExponentialValue(vector, lookAheadTileId);
                            lookAheadY++;
                        } while (lookAheadY < DIMENSION_VECTOR_LENGTH && lookAheadExponentialValue == ValuedTileSupport.EMPTY_TILE_VALUE);
                    }

                    int writeTileId = ValuedTileSupport.calculateTileId(actionIdType, x, writeY);

                    if (exponentialValue == lookAheadExponentialValue) {
                        int nextExponentialValue = exponentialValue + 1;

                        nextVector = ValuedTileSupport.mergeExponentialValue(nextVector, writeTileId, nextExponentialValue);
                        nextHighestExponentialValue = Math.max(nextHighestExponentialValue, nextExponentialValue);
                        nextScore += ValuedTileSupport.toDisplayValue(nextExponentialValue);
                        readY = lookAheadY - 1;
                    } else {
                        nextVector = ValuedTileSupport.mergeExponentialValue(nextVector, writeTileId, exponentialValue);

                        if (lookAheadExponentialValue != ValuedTileSupport.EMPTY_TILE_VALUE) {
                            readY = lookAheadY - 2;
                        } else {
                            readY = lookAheadY;
                        }
                    }

                    nextValuedTileCount++;
                    lastWrittenY = writeY++;
                }
            }

            for (int y = lastWrittenY + 1; y < DIMENSION_VECTOR_LENGTH; y++) {
                int tileId = ValuedTileSupport.calculateTileId(actionIdType, x, y);

                nextFreedTileIdToTileIdMappingVector = ValuedTileSupport.mergeTileId(nextFreedTileIdToTileIdMappingVector, freedTileId, tileId);
                nextTileIdToFreedTileIdMappingVector = ValuedTileSupport.mergeTileId(nextTileIdToFreedTileIdMappingVector, tileId, freedTileId + 1);
                freedTileId++;
            }
        }

        return new Board(nextVector, EMPTY_VALUED_TILES_ADDED, nextValuedTileCount, nextFreedTileIdToTileIdMappingVector, nextTileIdToFreedTileIdMappingVector, ActionIdSupport.EMPTY_VECTOR, nextHighestExponentialValue, nextScore);
    }

    public Board createNext(final List<ValuedTile> valuedTiles) {
        long nextVector = vector;
        int nextValuedTileCount = valuedTileCount;
        int nextHighestExponentialValue = highestExponentialValue;

        for (ValuedTile valuedTile : valuedTiles) {
            nextVector = ValuedTileSupport.mergeExponentialValue(nextVector, valuedTile);
            nextValuedTileCount++;
            nextHighestExponentialValue = Math.max(nextHighestExponentialValue, valuedTile.getExponentialValue());
        }

        int nextActionIdsAllowedVector = ActionIdSupport.determineIsAllowedGivenBoard(nextVector);

        return new Board(nextVector, valuedTiles, nextValuedTileCount, ValuedTileSupport.EMPTY_FREED_ID_TO_ID_MAPPING_VECTOR, ValuedTileSupport.EMPTY_FREED_ID_TO_ID_MAPPING_VECTOR, nextActionIdsAllowedVector, nextHighestExponentialValue, score);
    }

    private static String getActionIdsAllowedText(final int actionIdsAllowedVector) {
        StringBuilder stringBuilder = new StringBuilder();

        if (ActionIdSupport.isAllowed(actionIdsAllowedVector, ActionIdType.LEFT.getValue())) {
            stringBuilder.append("L");
        } else {
            stringBuilder.append("_");
        }

        if (ActionIdSupport.isAllowed(actionIdsAllowedVector, ActionIdType.UP.getValue())) {
            stringBuilder.append("U");
        } else {
            stringBuilder.append("_");
        }

        if (ActionIdSupport.isAllowed(actionIdsAllowedVector, ActionIdType.RIGHT.getValue())) {
            stringBuilder.append("R");
        } else {
            stringBuilder.append("_");
        }

        if (ActionIdSupport.isAllowed(actionIdsAllowedVector, ActionIdType.DOWN.getValue())) {
            stringBuilder.append("D");
        } else {
            stringBuilder.append("_");
        }

        return stringBuilder.toString();
    }

    public void print(final PrintStream stream, final int depth, final ActionIdType actionIdType) {
        stream.println("=============================");
        stream.printf("depth: %d%n", depth);
        stream.printf("actions allowed: %s%n", getActionIdsAllowedText(intentionalActionIdsAllowedVector));
        stream.printf("highest value: %s%n", ValuedTileSupport.toDisplayValue(highestExponentialValue));
        stream.printf("score: %s%n", score);

        if (actionIdType != null) {
            stream.printf("last action: %s%n", actionIdType);
        } else {
            stream.printf("last action: added tile%n");
        }

        for (int x = 0; x < DIMENSION_VECTOR_LENGTH; x++) {
            stream.print("|");

            for (int y = 0; y < DIMENSION_VECTOR_LENGTH; y++) {
                int tileId = ValuedTileSupport.calculateTileId(ActionIdType.LEFT, x, y);
                int exponentialValue = ValuedTileSupport.extractExponentialValue(vector, tileId);
                int displayValue = ValuedTileSupport.toDisplayValue(exponentialValue);

                if (displayValue >= 2) {
                    stream.printf("%1$5s |", displayValue);
                } else {
                    stream.print("      |");
                }
            }

            stream.println();
        }

        stream.println("=============================");
    }
}
