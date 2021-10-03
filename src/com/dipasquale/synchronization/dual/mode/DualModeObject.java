package com.dipasquale.synchronization.dual.mode;

import java.util.Map;

public interface DualModeObject {
    int concurrencyLevel();

    void activateMode(int concurrencyLevel);

    static <T extends DualModeObject> T activateMode(final T dualObject, final int concurrencyLevel) {
        dualObject.activateMode(concurrencyLevel);

        return dualObject;
    }

    static <T extends Iterable<? extends DualModeObject>> T forEachActivateMode(final T dualObjects, final int concurrencyLevel) {
        for (DualModeObject dualObject : dualObjects) {
            activateMode(dualObject, concurrencyLevel);
        }

        return dualObjects;
    }

    static <T extends Map<?, ? extends DualModeObject>> T forEachValueActivateMode(final T dualObjects, final int concurrencyLevel) {
        forEachActivateMode(dualObjects.values(), concurrencyLevel);

        return dualObjects;
    }
}
