package com.dipasquale.simulation.game2048.heuristic;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Arrays;

@Getter(AccessLevel.PACKAGE)
public enum WeightedBoardType {
    SNAKE_SHAPE(createSnakeShapeWeights()),
    CORNER(createCornerWeights()),
    DESCENDING(createDescendingWeights());

    private static double[] createSnakeShapeWeights() {
        return new double[]{
                Math.pow(5D, 6D), Math.pow(5D, 5D), Math.pow(5D, 4D), Math.pow(5D, 3D),
                Math.pow(5D, 1D), Math.pow(5D, 1.5D), Math.pow(5D, 2D), Math.pow(5D, 2.5D),
                Math.pow(5D, -1D), Math.pow(5D, -1.5D), Math.pow(5D, -2D), Math.pow(5D, -2.5D),
                Math.pow(5D, -6D), Math.pow(5D, -5D), Math.pow(5D, -4D), Math.pow(5D, -3D)
        };
    }

    private static double[] createCornerWeights() {
        return new double[]{
                Math.pow(5D, 4D), Math.pow(5D, 1D), Math.pow(5D, 1D), Math.pow(5D, 4D),
                Math.pow(5D, 1D), 0D, 0D, Math.pow(5D, 1D),
                Math.pow(5D, 1D), 0D, 0D, Math.pow(5D, 1D),
                Math.pow(5D, 4D), Math.pow(5D, 1D), Math.pow(5D, 1D), Math.pow(5D, 4D)
        };
    }

    private static double[] createDescendingWeights() {
        return new double[]{
                Math.pow(5D, 6D), Math.pow(5D, 5D), Math.pow(5D, 4D), Math.pow(5D, 3D),
                Math.pow(5D, 5D), Math.pow(5D, 4D), Math.pow(5D, 3D), Math.pow(5D, 2D),
                Math.pow(5D, 4D), Math.pow(5D, 3D), Math.pow(5D, 2D), Math.pow(5D, 1D),
                Math.pow(5D, 3D), Math.pow(5D, 2D), Math.pow(5D, 1D), Math.pow(5D, 0D)
        };
    }

    private static double[] createSortedWeights(final double[] weights) {
        double[] copiedWeights = Arrays.copyOf(weights, weights.length);

        Arrays.sort(copiedWeights);

        for (int i = 0, c = copiedWeights.length / 2; i < c; i++) {
            int iEnd = copiedWeights.length - i - 1;
            double weight = copiedWeights[iEnd];

            copiedWeights[iEnd] = copiedWeights[i];
            copiedWeights[i] = weight;
        }

        return copiedWeights;
    }

    private final double[] weights;
    private final double[] sortedWeights;

    WeightedBoardType(final double[] weights) {
        this.weights = weights;
        this.sortedWeights = createSortedWeights(weights);
    }
}
