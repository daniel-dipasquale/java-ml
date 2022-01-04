package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Builder
public final class RoundRobinDuelNeatEnvironment implements SharedNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -4718577446760598268L;
    private final ContestNeatEnvironment environment;
    @Builder.Default
    private final float matchesRate = 1f;
    @Builder.Default
    private final int rematches = 1;

    private List<Match> createMatches(final SharedGenomeActivator sharedGenomeActivator) {
        List<Match> matches = new ArrayList<>();
        List<GenomeActivator> genomeActivators = sharedGenomeActivator.getGenomeActivators();
        int originalSize = genomeActivators.size();
        double maximumExponent = Math.log(originalSize) / Math.log(2D);
        int maximumSize = (int) Math.pow(2D, maximumExponent);
        double adjustedExponent = Math.floor(maximumExponent * matchesRate);
        int adjustedSize = (int) Math.pow(2D, adjustedExponent);

        for (int remainderSize = originalSize; remainderSize > 1; ) {
            int matchCount = adjustedSize - 1;

            for (int i1 = originalSize - remainderSize; i1 < maximumSize; i1 += adjustedSize) {
                for (int i2 = i1, c2 = i1 + matchCount; i2 < c2; i2++) {
                    GenomeActivator genomeActivator1 = genomeActivators.get(i2);

                    for (int i3 = i2 + 1, c3 = i1 + adjustedSize; i3 < c3; i3++) {
                        GenomeActivator genomeActivator2 = genomeActivators.get(i3);
                        Match match = new Match(genomeActivator1, genomeActivator2);

                        matches.add(match);
                    }
                }
            }

            remainderSize -= maximumSize;
            maximumExponent = Math.log(remainderSize) / Math.log(2D);
            maximumSize = (int) Math.pow(2D, maximumExponent);
        }

        return matches;
    }

    private void test(final SharedGenomeActivator sharedGenomeActivator, final Match match) {
        int rematch = 0;

        for (Match currentMatch = match; rematch++ <= rematches; currentMatch = currentMatch.next) {
            float[] fitnessValues = environment.test(currentMatch.genomeActivators);

            for (int i = 0; i < currentMatch.genomeActivators.length; i++) {
                sharedGenomeActivator.addFitness(currentMatch.genomeActivators[i], fitnessValues[i]);
            }
        }
    }

    @Override
    public void test(final SharedGenomeActivator sharedGenomeActivator) {
        List<Match> matches = createMatches(sharedGenomeActivator);
        WaitHandle waitHandle = sharedGenomeActivator.getContext().parallelism().forEach(matches, m -> test(sharedGenomeActivator, m));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted while all genomes were dueling", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Match {
        private final GenomeActivator[] genomeActivators;
        private final Match next;

        private Match(final GenomeActivator genomeActivator1, final GenomeActivator genomeActivator2) {
            this.genomeActivators = new GenomeActivator[]{genomeActivator1, genomeActivator2};
            this.next = new Match(new GenomeActivator[]{genomeActivator2, genomeActivator1}, this);
        }
    }
}
