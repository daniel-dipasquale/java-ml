package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.rl.neat.core.RecurrentStateType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.data.structure.iterator.ZipIterator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class ProxyRecurrentModifiersFactory implements RecurrentModifiersFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 4439420908217479283L;
    private final FloatFactory modifierFactory;
    private final int size;

    public ProxyRecurrentModifiersFactory(final FloatFactory modifierFactory, final RecurrentStateType recurrentStateType) {
        this.modifierFactory = modifierFactory;
        this.size = recurrentStateType.getModifiers();
    }

    @Override
    public List<Float> create() {
        return IntStream.range(0, size)
                .mapToObj(__ -> modifierFactory.create())
                .collect(Collectors.toList());
    }

    @Override
    public List<Float> clone(final List<Float> recurrentWeights) {
        return new ArrayList<>(recurrentWeights);
    }

    @Override
    public List<Float> createAverage(final List<Float> recurrentWeights1, final List<Float> recurrentWeights2) {
        List<Iterator<Float>> iterators = List.of(recurrentWeights1.iterator(), recurrentWeights2.iterator());
        Iterable<List<Float>> iterable = () -> new ZipIterator<>(iterators);

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(recurrentWeights -> (recurrentWeights.get(0) + recurrentWeights.get(1)) / 2f)
                .collect(Collectors.toList());
    }
}
