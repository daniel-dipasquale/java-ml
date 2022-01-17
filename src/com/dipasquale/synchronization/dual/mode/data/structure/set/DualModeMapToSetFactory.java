package com.dipasquale.synchronization.dual.mode.data.structure.set;

import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class DualModeMapToSetFactory implements SetFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 4208747807462402226L;
    private final DualModeMapFactory mapFactory;

    @Override
    public <T> Set<T> create(final Set<T> other) {
        Map<T, Boolean> map = mapFactory.create(null);
        Set<T> set = Collections.newSetFromMap(map);

        if (other != null) {
            set.addAll(other);
        }

        return set;
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        mapFactory.activateMode(concurrencyLevel);
    }
}
