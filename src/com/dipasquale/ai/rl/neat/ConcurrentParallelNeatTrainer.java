package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.io.IORuntimeException;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import com.dipasquale.synchronization.lock.NoopReadWriteLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class ConcurrentParallelNeatTrainer implements ParallelNeatTrainer {
    private static final Comparator<IndexedNeatTrainer> MAXIMUM_FITNESS_COMPARATOR = Comparator.comparing(indexedTrainer -> indexedTrainer.trainer.getState().getMaximumFitness());
    private final ReadWriteLock lock;
    private final Context.ParallelismSupport parallelismSupport;
    private final AtomicBoolean solutionFound;
    private final List<IndexedNeatTrainer> indexedTrainers;
    private volatile int championTrainerIndex;
    private final ConcurrentNeatState state;

    private ConcurrentParallelNeatTrainer(final ReadWriteLock lock, final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<IndexedNeatTrainer> indexedTrainers, final int championTrainerIndex) {
        this.lock = lock;
        this.parallelismSupport = parallelismSupport;
        this.solutionFound = solutionFound;
        this.indexedTrainers = indexedTrainers;
        this.championTrainerIndex = championTrainerIndex;
        this.state = new ConcurrentNeatState();
    }

    private ConcurrentParallelNeatTrainer(final ReadWriteLock lock, final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<IndexedNeatTrainer> indexedTrainers) {
        this(lock, parallelismSupport, solutionFound, indexedTrainers, 0);
    }

    private static List<IndexedNeatTrainer> createTrainers(final List<Context> contexts, final ParallelTrainingPolicy parallelTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        return IntStream.range(0, contexts.size())
                .mapToObj(index -> new IndexedNeatTrainer(index, createTrainer(contexts.get(index), parallelTrainingPolicy, trainingPolicy)))
                .collect(Collectors.toList());
    }

    private ConcurrentParallelNeatTrainer(final ReadWriteLock lock, final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<Context> contexts, final ParallelTrainingPolicy parallelTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        this(lock, parallelismSupport, solutionFound, createTrainers(contexts, parallelTrainingPolicy, trainingPolicy));
    }

    private ConcurrentParallelNeatTrainer(final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<Context> contexts, final NeatTrainingPolicy trainingPolicy) {
        this(new ReentrantReadWriteLock(), parallelismSupport, solutionFound, contexts, new ParallelTrainingPolicy(solutionFound), trainingPolicy);
    }

    ConcurrentParallelNeatTrainer(final Context.ParallelismSupport parallelismSupport, final List<Context> contexts, final NeatTrainingPolicy trainingPolicy) {
        this(parallelismSupport, new AtomicBoolean(), contexts, trainingPolicy);
    }

    private static NeatTrainer createTrainer(final Context context, final ParallelTrainingPolicy parallelTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        NeatTrainingPolicy fixedTrainingPolicy = NeatTrainingPolicyController.builder()
                .add(parallelTrainingPolicy)
                .add(trainingPolicy.createClone())
                .build();

        return new ConcurrentNeatTrainer(NoopReadWriteLock.getInstance(), context, fixedTrainingPolicy);
    }

    @Override
    public NeatState getState() {
        return state;
    }

    private NeatTrainer getMostEfficientTrainer() {
        return indexedTrainers.get(championTrainerIndex).trainer;
    }

    @Override
    public NeatNeuronMemory createMemory() {
        lock.readLock().lock();

        try {
            return getMostEfficientTrainer().createMemory();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input, final NeatNeuronMemory neuronMemory) {
        lock.readLock().lock();

        try {
            return getMostEfficientTrainer().activate(input, neuronMemory);
        } finally {
            lock.readLock().unlock();
        }
    }

    private static int getChampionTrainerIndex(final SynchronizingTrainingHandler trainingHandler, final int defaultChampionTrainerIndex) {
        return trainingHandler.successfulIndexedTrainers.stream()
                .max(MAXIMUM_FITNESS_COMPARATOR)
                .map(indexedTrainer -> indexedTrainer.index)
                .orElse(defaultChampionTrainerIndex);
    }

    @Override
    public boolean train() {
        lock.writeLock().lock();

        try {
            SynchronizingTrainingHandler trainingHandler = new SynchronizingTrainingHandler(solutionFound);

            parallelismSupport.forEach(indexedTrainers, trainingHandler);
            championTrainerIndex = getChampionTrainerIndex(trainingHandler, championTrainerIndex);

            return !trainingHandler.successfulIndexedTrainers.isEmpty();
        } finally {
            solutionFound.set(false);
            lock.writeLock().unlock();
        }
    }

    @Override
    public NeatTrainingResult test() {
        lock.readLock().lock();

        try {
            return getMostEfficientTrainer().test();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        lock.writeLock().lock();

        try {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeInt(indexedTrainers.size());
                objectOutputStream.writeBoolean(solutionFound.get());
                objectOutputStream.writeInt(championTrainerIndex);
            }

            for (IndexedNeatTrainer indexedTrainer : indexedTrainers) {
                indexedTrainer.trainer.save(outputStream);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static Context.ParallelismSupport createParallelismSupport(final ParallelEventLoop eventLoop) {
        return ParallelismSettings.builder()
                .eventLoop(eventLoop)
                .build()
                .create();
    }

    static ConcurrentParallelNeatTrainer create(final InputStream inputStream, final NeatLoadSettings loadSettings)
            throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            int indexTrainerCount;
            AtomicBoolean solutionFound;
            int championTrainerIndex;

            try {
                indexTrainerCount = objectInputStream.readInt();
            } catch (IOException e) {
                throw new IOException("unable to load the multi instance trainer: failed at loading the index trainer count", e);
            }

            try {
                solutionFound = new AtomicBoolean(objectInputStream.readBoolean());
            } catch (IOException e) {
                throw new IOException("unable to load the multi instance trainer: failed at loading the solution found synchronization flag", e);
            }

            try {
                championTrainerIndex = objectInputStream.readInt();
            } catch (IOException e) {
                throw new IOException("unable to load the multi instance trainer: failed at loading the champion index indicator", e);
            }

            NeatLoadSettings fixedLoadSettings = NeatLoadSettings.builder()
                    .fitnessFunction(loadSettings.getFitnessFunction())
                    .build();

            List<IndexedNeatTrainer> indexedNeatTrainers = new ArrayList<>();

            try {
                for (int i = 0; i < indexTrainerCount; i++) {
                    ConcurrentNeatTrainer neatTrainer = ConcurrentNeatTrainer.create(inputStream, fixedLoadSettings);
                    IndexedNeatTrainer indexedNeatTrainer = new IndexedNeatTrainer(i, neatTrainer);

                    indexedNeatTrainers.add(indexedNeatTrainer);
                }
            } catch (IOException e) {
                throw new IOException("unable to load the multi instance trainer: failed at loading one of the trainers", e);
            }

            return new ConcurrentParallelNeatTrainer(new ReentrantReadWriteLock(), createParallelismSupport(loadSettings.getEventLoop()), solutionFound, indexedNeatTrainers, championTrainerIndex);
        }
    }

    @Override
    public NeatTrainer cloneMostEfficientTrainer(final NeatSettingsOverride settingsOverride) {
        NeatTrainer trainer = getMostEfficientTrainer();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            trainer.save(outputStream);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                return Neat.createTrainer(inputStream, settingsOverride);
            }
        } catch (IOException e) {
            throw new IORuntimeException("unable to clone the most efficient neat trainer", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SynchronizingTrainingHandler implements Consumer<IndexedNeatTrainer> {
        private final AtomicBoolean solutionFound;
        private final Deque<IndexedNeatTrainer> successfulIndexedTrainers = new ConcurrentLinkedDeque<>();

        @Override
        public void accept(final IndexedNeatTrainer indexedTrainer) {
            if (!indexedTrainer.trainer.train()) {
                return;
            }

            solutionFound.set(true);
            successfulIndexedTrainers.add(indexedTrainer);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class IndexedNeatTrainer {
        private final int index;
        private final NeatTrainer trainer;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ConcurrentNeatState implements NeatState {
        private int getIteration(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().getIteration();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getIteration() {
            return getIteration(lock.readLock());
        }

        private int getGeneration(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().getGeneration();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getGeneration() {
            return getGeneration(lock.readLock());
        }

        private int getSpeciesCount(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().getSpeciesCount();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getSpeciesCount() {
            return getSpeciesCount(lock.readLock());
        }

        private Genome getChampionGenome(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().getChampionGenome();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Genome getChampionGenome() {
            return getChampionGenome(lock.readLock());
        }

        private float getMaximumFitness(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().getMaximumFitness();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public float getMaximumFitness() {
            return getMaximumFitness(lock.readLock());
        }

        private MetricsViewer createMetricsViewer(final Lock lock) {
            lock.lock();

            try {
                return getMostEfficientTrainer().getState().createMetricsViewer();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public MetricsViewer createMetricsViewer() {
            return createMetricsViewer(lock.readLock());
        }
    }
}
