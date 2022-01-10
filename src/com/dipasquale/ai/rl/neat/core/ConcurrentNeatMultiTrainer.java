package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.io.IORuntimeException;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.lock.NoopReadWriteLock;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class ConcurrentNeatMultiTrainer implements MultiNeatTrainer {
    private static final Comparator<IndexedNeatTrainer> MAXIMUM_FITNESS_COMPARATOR = Comparator.comparing(indexedTrainer -> indexedTrainer.trainer.getState().getMaximumFitness());
    private final ReadWriteLock lock;
    private volatile Context.ParallelismSupport parallelismSupport;
    private final AtomicBoolean solutionFound;
    private final List<IndexedNeatTrainer> indexedTrainers;
    private volatile int championTrainerIndex;
    private final ConcurrentNeatState state;

    private ConcurrentNeatMultiTrainer(final ReadWriteLock lock, final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<IndexedNeatTrainer> indexedTrainers) {
        this.lock = lock;
        this.parallelismSupport = parallelismSupport;
        this.solutionFound = solutionFound;
        this.indexedTrainers = indexedTrainers;
        this.championTrainerIndex = 0;
        this.state = new ConcurrentNeatState();
    }

    private ConcurrentNeatMultiTrainer(final ReadWriteLock lock, final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<Context> contexts, final MultiTrainingPolicy multiTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        this(lock, parallelismSupport, solutionFound, createTrainers(contexts, multiTrainingPolicy, trainingPolicy));
    }

    private ConcurrentNeatMultiTrainer(final Context.ParallelismSupport parallelismSupport, final AtomicBoolean solutionFound, final List<Context> contexts, final NeatTrainingPolicy trainingPolicy) {
        this(new ReentrantReadWriteLock(), parallelismSupport, solutionFound, contexts, new MultiTrainingPolicy(solutionFound), trainingPolicy);
    }

    ConcurrentNeatMultiTrainer(final Context.ParallelismSupport parallelismSupport, final List<Context> contexts, final NeatTrainingPolicy trainingPolicy) {
        this(parallelismSupport, new AtomicBoolean(), contexts, trainingPolicy);
    }

    private static NeatTrainer createTrainer(final Context context, final MultiTrainingPolicy multiTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        NeatTrainingPolicy trainingPolicyFixed = NeatTrainingPolicies.builder()
                .add(multiTrainingPolicy)
                .add(trainingPolicy.createClone())
                .build();

        return new ConcurrentNeatTrainer(context, trainingPolicyFixed, NoopReadWriteLock.getInstance());
    }

    private static List<IndexedNeatTrainer> createTrainers(final List<Context> contexts, final MultiTrainingPolicy multiTrainingPolicy, final NeatTrainingPolicy trainingPolicy) {
        return IntStream.range(0, contexts.size())
                .mapToObj(index -> new IndexedNeatTrainer(index, createTrainer(contexts.get(index), multiTrainingPolicy, trainingPolicy)))
                .collect(Collectors.toList());
    }

    @Override
    public NeatState getState() {
        return state;
    }

    private NeatTrainer getMostEfficientTrainer() {
        return indexedTrainers.get(championTrainerIndex).trainer;
    }

    @Override
    public NeuronMemory createMemory() {
        lock.readLock().lock();

        try {
            return getMostEfficientTrainer().createMemory();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
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
            WaitHandle waitHandle = parallelismSupport.forEach(indexedTrainers, trainingHandler);

            waitHandle.await();
            championTrainerIndex = getChampionTrainerIndex(trainingHandler, championTrainerIndex);

            return !trainingHandler.successfulIndexedTrainers.isEmpty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("unable to train all neat evaluators", e);
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
                objectOutputStream.writeObject(solutionFound.get());
                objectOutputStream.writeObject(championTrainerIndex);
            }

            for (IndexedNeatTrainer indexedTrainer : indexedTrainers) {
                indexedTrainer.trainer.save(outputStream);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static boolean isEquivalent(final Context.ParallelismSupport parallelismSupport, final IterableEventLoop eventLoop) {
        return !parallelismSupport.params().enabled() && eventLoop == null
                || parallelismSupport.params().enabled() && eventLoop != null && parallelismSupport.params().numberOfThreads() == eventLoop.getConcurrencyLevel();
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorLoadSettings settings)
            throws IOException {
        lock.writeLock().lock();

        if (!isEquivalent(parallelismSupport, settings.getEventLoop())) {
            throw new IOException("unable to override event loop");
        }

        try {
            EvaluatorLoadSettings settingsFixed = EvaluatorLoadSettings.builder()
                    .fitnessFunction(settings.getFitnessFunction())
                    .build();

            try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                solutionFound.set((boolean) objectInputStream.readObject());
                championTrainerIndex = (int) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to load the multi instance trainer", e);
            }

            for (IndexedNeatTrainer indexedTrainer : indexedTrainers) {
                indexedTrainer.trainer.load(inputStream, settingsFixed);
            }

            parallelismSupport = ParallelismSupport.builder()
                    .eventLoop(settings.getEventLoop())
                    .build()
                    .create();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public NeatTrainer cloneMostEfficientTrainer(final EvaluatorOverrideSettings settings) {
        NeatTrainer trainer = getMostEfficientTrainer();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            trainer.save(outputStream);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                return Neat.createTrainer(inputStream, settings);
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
        @Override
        public int getIteration() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().getIteration();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public int getGeneration() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().getGeneration();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public int getSpeciesCount() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().getSpeciesCount();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public Genome getChampionGenome() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().getChampionGenome();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public float getMaximumFitness() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().getMaximumFitness();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public MetricsViewer createMetricsViewer() {
            lock.readLock().lock();

            try {
                return getMostEfficientTrainer().getState().createMetricsViewer();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
