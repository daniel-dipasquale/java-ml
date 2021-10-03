package com.dipasquale.synchronization.dual.mode.data.structure.map;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public final class DualModeMapFactory implements MapFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 731527917656613853L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final DualModeFactory<MapFactory> mapFactoryCreator;
    private MapFactory mapFactory;

    private DualModeMapFactory(final ConcurrencyLevelState concurrencyLevelState, final DualModeFactory<MapFactory> mapFactoryCreator) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.mapFactoryCreator = mapFactoryCreator;
        this.mapFactory = mapFactoryCreator.create(concurrencyLevelState);
    }

    public DualModeMapFactory(final int concurrencyLevel, final int maximumConcurrencyLevel, final DualModeFactory<MapFactory> mapFactoryCreator) {
        this(new ConcurrencyLevelState(concurrencyLevel, maximumConcurrencyLevel), mapFactoryCreator);
    }

    public DualModeMapFactory(final int concurrencyLevel, final int maximumConcurrencyLevel) {
        this(concurrencyLevel, maximumConcurrencyLevel, HashMapFactoryCreator.getInstance());
    }

    @Override
    public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
        return mapFactory.create(other);
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
        mapFactory = mapFactoryCreator.create(concurrencyLevelState);
    }
}
