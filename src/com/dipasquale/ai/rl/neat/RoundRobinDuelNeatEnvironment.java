package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
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
public final class RoundRobinDuelNeatEnvironment implements CommunalNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -4718577446760598268L;
    private static final Comparator<Double> COMPARATOR = Double::compareTo;
    private final ContestedNeatEnvironment environment;
    private final int approximateMatchesPerGenome;
    private final int rematches;
    private final int eliminationRounds;

    @Builder
    private static RoundRobinDuelNeatEnvironment create(final ContestedNeatEnvironment environment, final int approximateMatchesPerGenome, final int rematches, final int eliminationRounds) {
        int fixedApproximateMatchesPerGenome = approximateMatchesPerGenome <= 0
                ? Integer.MAX_VALUE
                : approximateMatchesPerGenome;

        int fixedRematches = Math.max(rematches, 0);
        int fixedEliminationRounds = Math.max(eliminationRounds, 0);

        return new RoundRobinDuelNeatEnvironment(environment, fixedApproximateMatchesPerGenome, fixedRematches, fixedEliminationRounds);
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

    private List<Match> createMatches(final NeatContext.RandomnessSupport randomnessSupport, final List<GenomeActivator> genomeActivators) {
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

            randomnessSupport.shuffle(genomeActivators);
            fillMatches(matches, genomeActivators, populationSize, leagueSize, approximateMatchCount);
        } else {
            fillMatches(matches, genomeActivators, populationSize, populationSize, defaultMatchCount);
        }

        return matches;
    }

    private void playMatch(final CommunalGenomeActivator communalGenomeActivator, final Match match, final int round) {
        int rematch = 0;

        for (Match currentMatch = match; rematch++ <= rematches; currentMatch = currentMatch.next) {
            float[] fitnessValues = environment.test(currentMatch.genomeActivators, round);

            for (int i = 0; i < 2; i++) {
                communalGenomeActivator.addFitness(currentMatch.genomeActivators.get(i), fitnessValues[i]);
            }
        }
    }

    private void playMatches(final NeatContext.ParallelismSupport parallelismSupport, final List<Match> matches, final CommunalGenomeActivator communalGenomeActivator, final int round) {
        parallelismSupport.forEach(matches, match -> playMatch(communalGenomeActivator, match, round));
    }

    @Override
    public void test(final CommunalGenomeActivator communalGenomeActivator) {
        NeatContext context = communalGenomeActivator.getContext();
        NeatContext.RandomnessSupport randomnessSupport = context.getRandomness();
        List<GenomeActivator> genomeActivators = communalGenomeActivator.getGenomeActivators();
        int possibleRounds = (int) (Math.log(genomeActivators.size()) / Math.log(2D));
        NeatContext.ParallelismSupport parallelismSupport = context.getParallelism();
        Comparator<GenomeActivator> mostFitComparator = new MostFitGenomeActivatorComparator(communalGenomeActivator);

        for (int i = 0, c = Math.min(possibleRounds, eliminationRounds + 1), limit = (int) Math.pow(2D, possibleRounds); i < c; ) {
            List<Match> matches = createMatches(randomnessSupport, genomeActivators);

            playMatches(parallelismSupport, matches, communalGenomeActivator, i);

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
        private final CommunalGenomeActivator communalGenomeActivator;

        @Override
        public int compare(final GenomeActivator genomeActivator1, final GenomeActivator genomeActivator2) {
            float fitness1 = communalGenomeActivator.getFitness(genomeActivator1);
            float fitness2 = communalGenomeActivator.getFitness(genomeActivator2);

            return Float.compare(fitness2, fitness1);
        }
    }
}
