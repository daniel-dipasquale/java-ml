package com.dipasquale.simulation.game2048;

import com.dipasquale.common.bit.VectorManipulatorSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ActionIdSupport {
    private static final int MAXIMUM_BITS_PER_ACTION_ID = 2;
    private static final VectorManipulatorSupport VECTOR_MANIPULATOR_SUPPORT = VectorManipulatorSupport.create(MAXIMUM_BITS_PER_ACTION_ID);
    static final int EMPTY_VECTOR = 0;

    static boolean isAllowed(final int vector, final int actionId) {
        return VECTOR_MANIPULATOR_SUPPORT.extract(vector, actionId) == 1;
    }

    private static boolean determineIsAllowedGivenBoard(final long boardVector, final ActionIdType actionIdType) {
        for (int x = 0; x < ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH; x++) {
            int firstFreedTileY = Integer.MAX_VALUE;
            int lastValuedTileY = -1;

            for (int y = 0; y < ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH; y++) {
                int tileId = ValuedTileSupport.calculateTileId(actionIdType, x, y);
                int exponentialValue = ValuedTileSupport.extractExponentialValue(boardVector, tileId);

                if (exponentialValue != ValuedTileSupport.EMPTY_TILE_VALUE) {
                    int lookAheadY = y + 1;
                    int lookAheadExponentialValue = ValuedTileSupport.EMPTY_TILE_VALUE;

                    if (lookAheadY < ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH) {
                        do {
                            int lookAheadTileId = ValuedTileSupport.calculateTileId(actionIdType, x, lookAheadY);

                            lookAheadExponentialValue = ValuedTileSupport.extractExponentialValue(boardVector, lookAheadTileId);

                            if (lookAheadExponentialValue == ValuedTileSupport.EMPTY_TILE_VALUE) {
                                firstFreedTileY = Math.min(firstFreedTileY, lookAheadY);
                            }

                            lookAheadY++;
                        } while (lookAheadY < ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH && lookAheadExponentialValue == ValuedTileSupport.EMPTY_TILE_VALUE);
                    }

                    if (exponentialValue == lookAheadExponentialValue) {
                        return true;
                    }

                    lastValuedTileY = y;

                    if (lookAheadExponentialValue != ValuedTileSupport.EMPTY_TILE_VALUE) {
                        y = lookAheadY - 2;
                    } else {
                        y = lookAheadY;
                    }
                } else {
                    firstFreedTileY = Math.min(firstFreedTileY, y);
                }
            }

            if (firstFreedTileY < lastValuedTileY) {
                return true;
            }
        }

        return false;
    }

    static int determineIsAllowedGivenBoard(final long boardVector) {
        int vector = EMPTY_VECTOR;

        for (ActionIdType actionIdType : ActionIdType.values()) {
            if (determineIsAllowedGivenBoard(boardVector, actionIdType)) {
                vector = VECTOR_MANIPULATOR_SUPPORT.merge(vector, actionIdType.getValue(), 1);
            }
        }

        return vector;
    }
}
