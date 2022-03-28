package com.dipasquale.simulation.game2048;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class OptimumValuedTile { // TODO: make these untranslated values
    private static final OptimumValuedTile INSTANCE = new OptimumValuedTile();
    private final List<OptimumMetricDatum> optimumMetricData = createOptimumMetrics();
    private volatile int supportedDepth = optimumMetricData.size() - 1;

    public static OptimumValuedTile getInstance() {
        return INSTANCE;
    }

    private static List<OptimumMetricDatum> createOptimumMetrics() {
        List<OptimumMetricDatum> optimumMetricData = new ArrayList<>();

        optimumMetricData.add(new OptimumMetricDatum(List.of()));
        optimumMetricData.add(new OptimumMetricDatum(List.of(2, 2)));

        return optimumMetricData;
    }

    private void ensureExpanded(final int depth) {
        synchronized (optimumMetricData) {
            int size = optimumMetricData.size();

            if (depth >= size) {
                for (int i1 = size; i1 <= depth; i1 += 2) {
                    OptimumMetricDatum optimumMetricDatum = optimumMetricData.get(i1 - 1);
                    List<Integer> newValuedTiles = new ArrayList<>();

                    for (int i2 = 0, c2 = optimumMetricDatum.valuedTiles.size(); i2 < c2; ) {
                        int value1 = optimumMetricDatum.valuedTiles.get(i2);
                        int i2Next = i2 + 1;

                        if (i2Next < c2 && value1 == optimumMetricDatum.valuedTiles.get(i2Next)) {
                            newValuedTiles.add(value1 + 1);
                            i2 += 2;
                        } else {
                            newValuedTiles.add(value1);
                            i2++;
                        }
                    }

                    optimumMetricData.add(new OptimumMetricDatum(List.copyOf(newValuedTiles)));
                    newValuedTiles.add(2);
                    optimumMetricData.add(new OptimumMetricDatum(List.copyOf(newValuedTiles)));
                }

                supportedDepth = optimumMetricData.size() - 1;
            }
        }
    }

    public List<Integer> getMaximumValuedTiles(final int depth) {
        if (depth > supportedDepth) {
            ensureExpanded(depth);
        }

        return optimumMetricData.get(depth).valuedTiles;
    }

    private static int getMinimumValueCount(final int value) {
        return (int) Math.pow(2D, value - 1);
    }

    private static List<Integer> createMaximumValuedTiles(final List<Integer> valuedTiles, final int size) {
        int index = valuedTiles.size();
        Stack<Integer> committedValues = new Stack<>();

        for (int temporarySize = index; index > 0 && size > temporarySize; temporarySize = index + committedValues.size()) {
            int value = valuedTiles.get(--index);
            int minimumValueCount = getMinimumValueCount(value);

            if (size < --temporarySize + minimumValueCount) {
                Deque<Integer> postponedCommittedValues = new LinkedList<>();
                Deque<Integer> remainingValues = new LinkedList<>();

                remainingValues.addLast(value - 1);
                remainingValues.addLast(value - 1);

                for (int remainingSize = size - temporarySize; !remainingValues.isEmpty(); ) {
                    int fixedRemainingSize = remainingSize - postponedCommittedValues.size();
                    int remainingValue = remainingValues.removeLast();

                    if (fixedRemainingSize <= remainingValues.size() + 1) {
                        postponedCommittedValues.addLast(remainingValue);

                        while (!remainingValues.isEmpty()) {
                            postponedCommittedValues.addLast(remainingValues.removeLast());
                        }
                    } else if (remainingValue > 1) {
                        remainingValues.addLast(remainingValue - 1);
                        remainingValues.addLast(remainingValue - 1);
                    } else {
                        postponedCommittedValues.addLast(1);
                    }
                }

                while (!postponedCommittedValues.isEmpty()) {
                    committedValues.push(postponedCommittedValues.removeFirst());
                }
            } else {
                for (int i = 0; i < minimumValueCount; i++) {
                    committedValues.push(1);
                }
            }
        }

        List<Integer> newValuedTiles = new ArrayList<>();

        for (int i = 0; i < index; i++) {
            newValuedTiles.add(valuedTiles.get(i));
        }

        for (int i = 0, c = size - newValuedTiles.size(); i < c; i++) {
            newValuedTiles.add(committedValues.pop());
        }

        return newValuedTiles;
    }

    public List<Integer> getMaximumValuedTiles(final int depth, final int size) {
        List<Integer> valuedTiles = getMaximumValuedTiles(depth);
        int actualSize = valuedTiles.size();

        if (actualSize == size) {
            return valuedTiles;
        }

        if (actualSize > size) {
            return valuedTiles.subList(0, size);
        }

        return createMaximumValuedTiles(valuedTiles, size);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OptimumMetricDatum {
        private final List<Integer> valuedTiles;
    }
}