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
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class RoundRobinDuelNeatEnvironment implements SharedNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -4718577446760598268L;
    private static final Comparator<Double> COMPARATOR = Double::compareTo;
    private final ContestNeatEnvironment environment;
    private final int approximateMatchesPerGenome;
    private final int rematches;
    private final int eliminationRounds;

    @Builder
    private static RoundRobinDuelNeatEnvironment create(final ContestNeatEnvironment environment, final int approximateMatchesPerGenome, final int rematches, final int eliminationRounds) {
        int approximateMatchesPerGenomeFixed = approximateMatchesPerGenome <= 0
                ? Integer.MAX_VALUE
                : approximateMatchesPerGenome;

        int rematchesFixed = Math.max(rematches, 0);
        int roundsFixed = Math.max(eliminationRounds, 0);

        return new RoundRobinDuelNeatEnvironment(environment, approximateMatchesPerGenomeFixed, rematchesFixed, roundsFixed);
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

    private static void fillMatches(final List<Match> matches, final List<GenomeActivator> genomeActivators, final int populationSize, final int leagueSize, final int matchCount) {
        for (int i1 = 0; i1 < populationSize; i1 += leagueSize) {
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

    private List<Match> createMatches(final Context.RandomSupport randomSupport, final List<GenomeActivator> genomeActivators) {
        List<Match> matches = new ArrayList<>();
        int populationSize = genomeActivators.size();
        int defaultMatchCount = populationSize - 1;
        int approximateMatchCount = Math.min(defaultMatchCount, approximateMatchesPerGenome);

        if (populationSize - approximateMatchCount > 1) {
            NavigableMap<Double, Integer> possibleLeagueSizes = new TreeMap<>(COMPARATOR);
            double leagueSizeExponent = Math.log(approximateMatchCount + 1) / Math.log(2D);

            addLeagueSizeIfCompatible(possibleLeagueSizes, Math.ceil(leagueSizeExponent), populationSize);
            addLeagueSizeIfCompatible(possibleLeagueSizes, Math.floor(leagueSizeExponent), populationSize);

            int leagueSize = getBestLeagueSize(possibleLeagueSizes, Math.round(leagueSizeExponent), populationSize, approximateMatchCount);

            randomSupport.shuffle(genomeActivators);
            fillMatches(matches, genomeActivators, populationSize, leagueSize, approximateMatchCount);
        } else {
            fillMatches(matches, genomeActivators, populationSize, populationSize, defaultMatchCount);
        }

        return matches;
    }

    private void playMatch(final SharedGenomeActivator sharedGenomeActivator, final Match match, final int round) {
        int rematch = 0;

        for (Match currentMatch = match; rematch++ <= rematches; currentMatch = currentMatch.next) {
            float[] fitnessValues = environment.test(currentMatch.genomeActivators, round);

            for (int i = 0; i < 2; i++) {
                sharedGenomeActivator.addFitness(currentMatch.genomeActivators.get(i), fitnessValues[i]);
            }
        }
    }

    private void playMatches(final Context.ParallelismSupport parallelismSupport, final List<Match> matches, final SharedGenomeActivator sharedGenomeActivator, final int round) {
        WaitHandle waitHandle = parallelismSupport.forEach(matches, match -> playMatch(sharedGenomeActivator, match, round));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted while all genomes were dueling", e);
        }
    }

    @Override
    public void test(final SharedGenomeActivator sharedGenomeActivator) {
        Context context = sharedGenomeActivator.getContext();
        Context.RandomSupport randomSupport = context.random();
        List<GenomeActivator> genomeActivators = sharedGenomeActivator.getGenomeActivators();
        int possibleRounds = (int) (Math.log(genomeActivators.size()) / Math.log(2D));
        Context.ParallelismSupport parallelismSupport = context.parallelism();
        Comparator<GenomeActivator> mostFitComparator = new MostFitGenomeActivatorComparator(sharedGenomeActivator);

        for (int i = 0, c = Math.min(possibleRounds, eliminationRounds + 1), limit = (int) Math.pow(2D, possibleRounds); i < c; ) {
            List<Match> matches = createMatches(randomSupport, genomeActivators);

            playMatches(parallelismSupport, matches, sharedGenomeActivator, i);

            if (++i < c) {
                genomeActivators = genomeActivators.stream()
                        .sorted(mostFitComparator)
                        .limit(limit /= 2)
                        .collect(Collectors.toList());
            }
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class MostFitGenomeActivatorComparator implements Comparator<GenomeActivator> {
        private final SharedGenomeActivator sharedGenomeActivator;

        @Override
        public int compare(final GenomeActivator genomeActivator1, final GenomeActivator genomeActivator2) {
            float fitness1 = sharedGenomeActivator.getFitness(genomeActivator1);
            float fitness2 = sharedGenomeActivator.getFitness(genomeActivator2);

            return Float.compare(fitness2, fitness1);
        }
    }
}
