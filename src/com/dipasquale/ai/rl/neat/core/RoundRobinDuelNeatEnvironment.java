package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@RequiredArgsConstructor
public final class RoundRobinDuelNeatEnvironment implements SharedNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -4718577446760598268L;
    private static final Comparator<Double> COMPARATOR = Double::compareTo;
    private final ContestNeatEnvironment environment;
    private final int approximateMatchesPerGenome;
    private final int rematches;

    @Builder
    private static RoundRobinDuelNeatEnvironment create(final ContestNeatEnvironment environment, final int approximateMatchesPerGenome, final int rematches) {
        int approximateMatchesPerGenomeFixed = approximateMatchesPerGenome <= 0
                ? Integer.MAX_VALUE
                : approximateMatchesPerGenome;

        int rematchesFixed = Math.max(rematches, 0);

        return new RoundRobinDuelNeatEnvironment(environment, approximateMatchesPerGenomeFixed, rematchesFixed);
    }

    private static void addLeagueSizeIfCompatible(final NavigableMap<Double, Integer> possibleLeagueSizes, final double leagueSizeExponent, final int populationSize) {
        int leagueSize = (int) Math.pow(2D, leagueSizeExponent);

        if (populationSize % leagueSize == 0) {
            possibleLeagueSizes.put(leagueSizeExponent, leagueSize);
        }
    }

    private static int getBestLeagueSize(final NavigableMap<Double, Integer> possibleLeagueSizes, final double leagueSizeExponent, final int populationSize, final int approximateMatchCount) {
        if (!possibleLeagueSizes.isEmpty()) {
            Integer leagueSize = possibleLeagueSizes.get(leagueSizeExponent);

            if (leagueSize != null) {
                return leagueSize;
            }

            return possibleLeagueSizes.lastEntry().getValue();
        }

        String message = String.format("Unable to fit an even amount of round robin duels for a population of %d genomes, where each genome should duel %d", populationSize, approximateMatchCount);

        throw new IllegalStateException(message);
    }

    private static void fillMatches(final List<Match> matches, final List<GenomeActivator> genomeActivators, final int leagueSize, final int matchCount) {
        int size = genomeActivators.size();

        for (int i1 = 0; i1 < size; i1 += leagueSize) {
            for (int i2 = i1, c2 = i1 + matchCount; i2 < c2; i2++) {
                GenomeActivator genomeActivator1 = genomeActivators.get(i2);

                for (int i3 = i2 + 1, c3 = i1 + leagueSize; i3 < c3; i3++) {
                    GenomeActivator genomeActivator2 = genomeActivators.get(i3);
                    Match match = new Match(genomeActivator1, genomeActivator2);

                    matches.add(match);
                }
            }
        }
    }

    private List<Match> createMatches(final SharedGenomeActivator sharedGenomeActivator) {
        List<Match> matches = new ArrayList<>();
        List<GenomeActivator> genomeActivators = sharedGenomeActivator.getGenomeActivators();
        int populationSize = genomeActivators.size();
        int approximateMatchCount = Math.min(populationSize - 1, approximateMatchesPerGenome);

        if (populationSize - approximateMatchCount > 1) {
            NavigableMap<Double, Integer> possibleLeagueSizes = new TreeMap<>(COMPARATOR);
            double leagueSizeExponent = Math.log(approximateMatchCount + 1) / Math.log(2D);

            addLeagueSizeIfCompatible(possibleLeagueSizes, Math.ceil(leagueSizeExponent), populationSize);
            addLeagueSizeIfCompatible(possibleLeagueSizes, Math.floor(leagueSizeExponent), populationSize);

            int leagueSize = getBestLeagueSize(possibleLeagueSizes, Math.round(leagueSizeExponent), populationSize, approximateMatchCount);

            sharedGenomeActivator.getContext().random().shuffle(genomeActivators);
            fillMatches(matches, genomeActivators, leagueSize, approximateMatchCount);
        } else {
            fillMatches(matches, genomeActivators, populationSize, populationSize - 1);
        }

        return matches;
    }

    private void playMatch(final SharedGenomeActivator sharedGenomeActivator, final Match match) {
        int rematch = 0;

        for (Match currentMatch = match; rematch++ <= rematches; currentMatch = currentMatch.next) {
            float[] fitnessValues = environment.test(currentMatch.genomeActivators);

            for (int i = 0; i < 2; i++) {
                sharedGenomeActivator.addFitness(currentMatch.genomeActivators.get(i), fitnessValues[i]);
            }
        }
    }

    @Override
    public void test(final SharedGenomeActivator sharedGenomeActivator) {
        List<Match> matches = createMatches(sharedGenomeActivator);
        WaitHandle waitHandle = sharedGenomeActivator.getContext().parallelism().forEach(matches, match -> playMatch(sharedGenomeActivator, match));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted while all genomes were dueling", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Match {
        private final List<GenomeActivator> genomeActivators;
        private final Match next;

        private Match(final GenomeActivator genomeActivator1, final GenomeActivator genomeActivator2) {
            this.genomeActivators = List.of(genomeActivator1, genomeActivator2);
            this.next = new Match(List.of(genomeActivator2, genomeActivator1), this);
        }
    }
}
