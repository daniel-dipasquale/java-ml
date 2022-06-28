package com.dipasquale.simulation.game2048;

import com.dipasquale.common.bit.VectorManipulatorSupport;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public final class ValuedTileSupport {
    static final int BOARD_DIMENSION_VECTOR_LENGTH = 4;
    static final int BOARD_VECTOR_LENGTH = BOARD_DIMENSION_VECTOR_LENGTH * BOARD_DIMENSION_VECTOR_LENGTH;
    static final int EMPTY_TILE_VALUE = 0;
    static final long EMPTY_BOARD_VECTOR = 0L;
    private static final int MAXIMUM_SPAWNING_EXPONENTIAL_VALUE = 2;
    static final int MAXIMUM_EXPONENTIAL_VALUE_PER_TILE = 16;
    private static final int MAXIMUM_BITS_PER_TILE = (int) Math.ceil(Math.log(MAXIMUM_EXPONENTIAL_VALUE_PER_TILE) / Math.log(2D));
    private static final VectorManipulatorSupport BOARD_VECTOR_MANIPULATOR_SUPPORT = VectorManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    static final long EMPTY_FREED_ID_TO_ID_MAPPING_VECTOR = 0L;
    private static final int MAXIMUM_BITS_PER_FREED_ID_TO_ID_MAPPING = (int) Math.ceil(Math.log(BOARD_VECTOR_LENGTH) / Math.log(2D));
    private static final VectorManipulatorSupport FREED_ID_TO_ID_MAPPING_VECTOR_SUPPORT = VectorManipulatorSupport.create(MAXIMUM_BITS_PER_FREED_ID_TO_ID_MAPPING);
    static final long INITIAL_FREED_ID_TO_ID_MAPPING_VECTOR = calculateInitialFreedTileIds();

    private static final int[] DISPLAY_VALUES = IntStream.range(1, MAXIMUM_EXPONENTIAL_VALUE_PER_TILE + 1)
            .map(index -> (int) Math.pow(2D, index))
            .toArray();

    private static final Map<Integer, Integer> EXPONENTIAL_VALUES = IntStream.range(0, DISPLAY_VALUES.length)
            .boxed()
            .collect(Collectors.toMap(index -> DISPLAY_VALUES[index], index -> index + 1));

    static final float PROBABILITY_OF_SPAWNING_2 = 0.9f;
    static final List<ValuedTile> EMPTY_VALUED_TILES = List.of();
    private final RandomSupport tileIdRandomSupport;
    private final RandomSupport exponentialValueRandomSupport;

    static int calculateTileId(final ActionIdType actionIdType, final int x, final int y) {
        return switch (actionIdType) {
            case LEFT -> BOARD_DIMENSION_VECTOR_LENGTH * x + y;

            case UP -> BOARD_DIMENSION_VECTOR_LENGTH * y + x;

            case RIGHT -> BOARD_DIMENSION_VECTOR_LENGTH * x + (BOARD_DIMENSION_VECTOR_LENGTH - y - 1);

            case DOWN -> BOARD_DIMENSION_VECTOR_LENGTH * (BOARD_DIMENSION_VECTOR_LENGTH - y - 1) + x;
        };
    }

    public static int extractExponentialValue(final int rowOrColumnVector, final int index) {
        return BOARD_VECTOR_MANIPULATOR_SUPPORT.extract(rowOrColumnVector, index);
    }

    static int extractExponentialValue(final long boardVector, final int tileId) {
        return (int) BOARD_VECTOR_MANIPULATOR_SUPPORT.extract(boardVector, tileId);
    }

    public static int mergeExponentialValue(final int rowOrColumnVector, final int index, final int exponentialValue) {
        return BOARD_VECTOR_MANIPULATOR_SUPPORT.merge(rowOrColumnVector, index, exponentialValue);
    }

    static long mergeExponentialValue(final long boardVector, final int tileId, final int exponentialValue) {
        return BOARD_VECTOR_MANIPULATOR_SUPPORT.merge(boardVector, tileId, exponentialValue);
    }

    static long mergeExponentialValue(final long boardVector, final ValuedTile valuedTile) {
        return mergeExponentialValue(boardVector, valuedTile.getTileId(), valuedTile.getExponentialValue());
    }

    static int extractTileId(final long tileIdsVector, final int freedTileId) {
        return (int) FREED_ID_TO_ID_MAPPING_VECTOR_SUPPORT.extract(tileIdsVector, freedTileId);
    }

    static long mergeTileId(final long tileIdsVector, final int freedTileId, final int tileId) {
        return FREED_ID_TO_ID_MAPPING_VECTOR_SUPPORT.merge(tileIdsVector, freedTileId, tileId);
    }

    private static long calculateInitialFreedTileIds() {
        long tileIdsVector = 0L;

        for (int i = 0; i < BOARD_VECTOR_LENGTH; i++) {
            tileIdsVector = mergeTileId(tileIdsVector, i, i);
        }

        return tileIdsVector;
    }

    static int toDisplayValue(final int exponentialValue) {
        if (exponentialValue <= 0) {
            return 0;
        }

        if (exponentialValue <= MAXIMUM_EXPONENTIAL_VALUE_PER_TILE) {
            return DISPLAY_VALUES[exponentialValue - 1];
        }

        return (int) Math.pow(2D, exponentialValue);
    }

    static int toExponentialValue(final int displayValue) {
        if (displayValue <= DISPLAY_VALUES[MAXIMUM_EXPONENTIAL_VALUE_PER_TILE - 1]) {
            return EXPONENTIAL_VALUES.get(displayValue);
        }

        double exponentialValue = Math.log(displayValue) / Math.log(2D);

        return (int) exponentialValue;
    }

    public int generateFreedTileId(final int occupied) {
        return tileIdRandomSupport.nextInteger(0, BOARD_VECTOR_LENGTH - occupied);
    }

    public int generateExponentialValue() {
        if (!exponentialValueRandomSupport.isLessThan(PROBABILITY_OF_SPAWNING_2)) {
            return MAXIMUM_SPAWNING_EXPONENTIAL_VALUE;
        }

        return MAXIMUM_SPAWNING_EXPONENTIAL_VALUE - 1;
    }
}
