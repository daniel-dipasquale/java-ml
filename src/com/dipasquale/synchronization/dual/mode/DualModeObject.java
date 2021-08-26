package com.dipasquale.synchronization.dual.mode;

import java.util.Map;

@FunctionalInterface
public interface DualModeObject {
    void switchMode(boolean concurrent);

    static <T extends DualModeObject> T switchMode(final T dualObject, final boolean concurrent) {
        dualObject.switchMode(concurrent);

        return dualObject;
    }

    static <T extends Iterable<? extends DualModeObject>> T switchModes(final T dualObjects, final boolean concurrent) {
        for (DualModeObject dualObject : dualObjects) {
            switchMode(dualObject, concurrent);
        }

        return dualObjects;
    }

    static <T extends Map<?, ? extends DualModeObject>> T switchModeMap(final T dualObjects, final boolean concurrent) {
        switchModes(dualObjects.values(), concurrent);

        return dualObjects;
    }
}
